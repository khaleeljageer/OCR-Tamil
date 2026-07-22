package com.jskaleel.vizhi_tamil.data.repository

import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.core.model.OCRResult
import com.jskaleel.vizhi_tamil.core.ocr.TrainedDataInstaller
import com.jskaleel.vizhi_tamil.core.utils.toRelativeTimeStamp
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.data.source.local.room.entity.RecentScan
import com.jskaleel.vizhi_tamil.data.source.local.storage.FileStorage
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface OCRRepository {
    /** Recognises text across all pages, persists one scan, and returns it with its id. */
    suspend fun recognizeAndSave(imagePaths: List<String>): OCRResult<ImageOCR>
    suspend fun getScan(id: Int): ImageOCR?
    suspend fun updateScanText(id: Int, text: String)
    fun getRecentScans(): Flow<List<ImageOCR>>
    suspend fun deleteScans(scans: List<ImageOCR>)
}

class OCRRepositoryImpl @Inject constructor(
    private val tessBaseAPI: TessBaseAPI,
    private val fileStorage: FileStorage,
    private val recentScanDao: RecentScanDao,
    private val trainedDataInstaller: TrainedDataInstaller,
) : OCRRepository {

    // Tesseract init is deferred off the constructor: it must run on a background
    // thread and only after the traineddata has been copied into place.
    private val initMutex = Mutex()

    @Volatile
    private var initialized = false

    // TessBaseAPI holds native per-call state and is not thread-safe, so
    // concurrent recognition is serialised.
    private val recognitionMutex = Mutex()

    private suspend fun ensureInitialized(): Result<Unit> {
        if (initialized) return Result.success(Unit)
        return initMutex.withLock {
            if (initialized) return@withLock Result.success(Unit)
            trainedDataInstaller.ensureInstalled().getOrElse {
                return@withLock Result.failure(it)
            }
            val ok = tessBaseAPI.init(fileStorage.getFilesDir().absolutePath, TESS_LANGUAGES)
            if (!ok) {
                return@withLock Result.failure(IllegalStateException("Tesseract init failed"))
            }
            tessBaseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD
            initialized = true
            Result.success(Unit)
        }
    }

    override suspend fun recognizeAndSave(
        imagePaths: List<String>,
    ): OCRResult<ImageOCR> = withContext(Dispatchers.IO) {
        if (imagePaths.isEmpty()) {
            return@withContext OCRResult.Error(message = "No image to scan")
        }
        ensureInitialized().getOrElse {
            return@withContext OCRResult.Error(
                message = it.message ?: "Failed to initialise OCR engine",
            )
        }

        val recognition = recognitionMutex.withLock { recognizePages(imagePaths) }
        if (recognition.text.isBlank()) {
            return@withContext OCRResult.Error(message = "No text found in the image")
        }

        val timeStamp = System.currentTimeMillis()
        val savedImagePath = copyImageToStorage(File(imagePaths.first()))
        val id = recentScanDao.insert(
            RecentScan(
                filePath = savedImagePath,
                timeStamp = timeStamp,
                text = recognition.text,
                accuracy = recognition.accuracy,
            ),
        ).toInt()

        OCRResult.Success(
            ImageOCR(
                id = id,
                text = recognition.text,
                accuracy = recognition.accuracy,
                timeStamp = timeStamp.toRelativeTimeStamp(),
                imagePath = savedImagePath,
            ),
        )
    }

    private fun recognizePages(imagePaths: List<String>): Recognition {
        val builder = StringBuilder()
        var confidenceSum = 0
        var pageCount = 0
        imagePaths.forEach { path ->
            tessBaseAPI.setImage(File(path))
            val pageText = runCatching { tessBaseAPI.getUTF8Text() }.getOrDefault("").trim()
            if (pageText.isNotEmpty()) {
                if (builder.isNotEmpty()) builder.append("\n\n")
                builder.append(pageText)
                confidenceSum += tessBaseAPI.meanConfidence()
                pageCount++
            }
            tessBaseAPI.clear()
        }
        val accuracy = if (pageCount > 0) confidenceSum / pageCount else 0
        return Recognition(text = builder.toString(), accuracy = accuracy)
    }

    override suspend fun getScan(id: Int): ImageOCR? = withContext(Dispatchers.IO) {
        recentScanDao.getById(id)?.toImageOCR()
    }

    override suspend fun updateScanText(id: Int, text: String) = withContext(Dispatchers.IO) {
        recentScanDao.updateText(id, text)
    }

    override fun getRecentScans(): Flow<List<ImageOCR>> {
        return recentScanDao.getAllScan().map { scans ->
            scans.map { it.toImageOCR() }
        }
    }

    override suspend fun deleteScans(scans: List<ImageOCR>) = withContext(Dispatchers.IO) {
        scans.forEach { scan ->
            runCatching { File(scan.imagePath).takeIf { it.exists() }?.delete() }
        }
        recentScanDao.deleteByIds(scans.map { it.id })
    }

    private fun RecentScan.toImageOCR(): ImageOCR = ImageOCR(
        id = id,
        text = text,
        accuracy = accuracy,
        timeStamp = timeStamp.toRelativeTimeStamp(),
        imagePath = filePath,
    )

    private fun copyImageToStorage(tmpImagePath: File): String {
        val ocrImageDir = fileStorage.getOCRImageDir()
        val newFile = File(ocrImageDir, tmpImagePath.name)
        if (!newFile.exists()) {
            tmpImagePath.copyTo(newFile, overwrite = true)
        }
        return newFile.path
    }

    private data class Recognition(val text: String, val accuracy: Int)

    companion object {
        private const val TESS_LANGUAGES = "tam+eng"
    }
}
