package com.jskaleel.vizhi_tamil.data.repository

import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.core.model.OCRResult
import com.jskaleel.vizhi_tamil.core.ocr.TrainedDataInstaller
import com.jskaleel.vizhi_tamil.core.utils.stripHtml
import com.jskaleel.vizhi_tamil.core.utils.toRelativeTimeStamp
import com.jskaleel.vizhi_tamil.data.model.ImageOCRResponseDTO
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
    suspend fun fetchTextFromImage(imagePath: String): OCRResult<ImageOCRResponseDTO>
    suspend fun saveImageResult(oCR: ImageOCRResponseDTO)
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
    // concurrent scans are serialised.
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

    override suspend fun fetchTextFromImage(
        imagePath: String,
    ): OCRResult<ImageOCRResponseDTO> = withContext(Dispatchers.IO) {
        ensureInitialized().getOrElse {
            return@withContext OCRResult.Error(
                message = it.message ?: "Failed to initialise OCR engine",
            )
        }

        recognitionMutex.withLock {
            val ocrImagePath: File = File(imagePath)
            val imgFileDirPath = copyImageFromTempToDirectory(ocrImagePath)
            tessBaseAPI.setImage(ocrImagePath)
            val text = try {
                tessBaseAPI.getHOCRText(1)
            } catch (_: Exception) {
                ""
            }
            val accuracy = tessBaseAPI.meanConfidence()
            val timeStamp = System.currentTimeMillis()
            tessBaseAPI.clear()

            if (text.isNotEmpty()) {
                OCRResult.Success(ImageOCRResponseDTO(text, accuracy, timeStamp, imgFileDirPath))
            } else {
                OCRResult.Error(message = "No text found in the image")
            }
        }
    }

    override suspend fun saveImageResult(oCR: ImageOCRResponseDTO) {
        recentScanDao.insert(
            RecentScan(
                filePath = oCR.imagePath,
                timeStamp = oCR.timeStamp,
                text = oCR.text,
                accuracy = oCR.accuracy,
            ),
        )
    }

    override fun getRecentScans(): Flow<List<ImageOCR>> {
        return recentScanDao.getAllScan().map { scans ->
            scans.map { recentScan ->
                ImageOCR(
                    id = recentScan.id,
                    text = recentScan.text.stripHtml(),
                    accuracy = recentScan.accuracy,
                    timeStamp = recentScan.timeStamp.toRelativeTimeStamp(),
                    imagePath = recentScan.filePath,
                )
            }
        }
    }

    override suspend fun deleteScans(scans: List<ImageOCR>) = withContext(Dispatchers.IO) {
        scans.forEach { scan ->
            runCatching { File(scan.imagePath).takeIf { it.exists() }?.delete() }
        }
        recentScanDao.deleteByIds(scans.map { it.id })
    }

    private fun copyImageFromTempToDirectory(tmpImagePath: File): String {
        val ocrImageDir = fileStorage.getOCRImageDir()
        val newFile = File(ocrImageDir, tmpImagePath.name)
        if (!newFile.exists()) {
            tmpImagePath.copyTo(newFile, overwrite = true)
        }
        return newFile.path
    }

    companion object {
        private const val TESS_LANGUAGES = "tam+eng"
    }
}
