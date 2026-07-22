package com.jskaleel.vizhi_tamil.core.ocr

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copies the bundled `tam`/`eng` traineddata from assets into the app's
 * `tessdata` directory. Single source of truth for the copy so both the
 * eager [TrainedDataCopyWorker] pre-warm and the lazy on-demand init in the
 * repository stay consistent.
 */
@Singleton
class TrainedDataInstaller @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()

    /** Suspending, deduplicated install used on the OCR path. */
    suspend fun ensureInstalled(languages: List<String> = DEFAULT_LANGUAGES): Result<Unit> =
        withContext(Dispatchers.IO) {
            mutex.withLock { copyMissing(languages) }
        }

    /** Blocking install for the WorkManager pre-warm (already off the main thread). */
    fun installBlocking(languages: List<String> = DEFAULT_LANGUAGES): Result<Unit> =
        copyMissing(languages)

    private fun copyMissing(languages: List<String>): Result<Unit> {
        val tessDataDir = File(context.filesDir, TESSDATA_DIR).apply {
            if (!exists()) mkdirs()
        }
        return try {
            languages.forEach { lang -> copyLanguage(tessDataDir, lang) }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private fun copyLanguage(tessDataDir: File, lang: String) {
        val target = File(tessDataDir, "$lang.traineddata")
        if (target.exists() && target.length() > 0L) return
        context.assets.open("$TESSDATA_DIR/$lang.traineddata").use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }

    companion object {
        const val TESSDATA_DIR = "tessdata"
        val DEFAULT_LANGUAGES = listOf("tam", "eng")
    }
}
