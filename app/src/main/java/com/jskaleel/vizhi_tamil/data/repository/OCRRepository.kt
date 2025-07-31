package com.jskaleel.vizhi_tamil.data.repository

import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.core.model.OCRResult
import com.jskaleel.vizhi_tamil.core.utils.toRelativeTimeStamp
import com.jskaleel.vizhi_tamil.data.model.ImageOCRResponseDTO
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.data.source.local.room.entity.RecentScan
import com.jskaleel.vizhi_tamil.data.source.local.storage.FileStorage
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

interface OCRRepository {
    fun fetchTextFromImage(imagePath: String): OCRResult<ImageOCRResponseDTO>
    suspend fun saveImageResult(oCR: ImageOCRResponseDTO)
    fun getRecentScans(): Flow<List<ImageOCR>>
}

class OCRRepositoryImpl @Inject constructor(
    private val tessBaseAPI: TessBaseAPI,
    private val fileStorage: FileStorage,
    private val recentScanDao: RecentScanDao
) : OCRRepository {

    init {
        val tessDataPath = fileStorage.getFilesDir().absolutePath
        tessBaseAPI.init(tessDataPath, "tam+eng")
        tessBaseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD
    }

    override fun fetchTextFromImage(imagePath: String): OCRResult<ImageOCRResponseDTO> {
        val ocrImagePath: File = imagePath.toFile()
        tessBaseAPI.setImage(ocrImagePath)
        val imgFileDirPath = copyImageFromTempToDirectory(ocrImagePath)
        val text = try {
            tessBaseAPI.getHOCRText(1)
        } catch (_: Exception) {
            ""
        }
        val accuracy = tessBaseAPI.meanConfidence()
        val timeStamp = System.currentTimeMillis()
        tessBaseAPI.clear()
        return if (text.isNotEmpty()) {
            OCRResult.Success(ImageOCRResponseDTO(text, accuracy, timeStamp, imgFileDirPath))
        } else {
            OCRResult.Error(null, "No text found")
        }
    }

    override suspend fun saveImageResult(oCR: ImageOCRResponseDTO) {
        recentScanDao.insert(
            RecentScan(
                filePath = oCR.imagePath,
                timeStamp = oCR.timeStamp,
                text = oCR.text,
                accuracy = oCR.accuracy
            )
        )
    }

    override fun getRecentScans(): Flow<List<ImageOCR>> {
        return recentScanDao.getAllScan().map {
            it.map { recentScan ->
                ImageOCR(
                    text = recentScan.text,
                    accuracy = recentScan.accuracy,
                    timeStamp = recentScan.timeStamp.toRelativeTimeStamp(),
                    imagePath = recentScan.filePath
                )
            }
        }
    }

    fun stopTesseract() {
        tessBaseAPI.stop()
        tessBaseAPI.recycle()
    }

    private fun copyImageFromTempToDirectory(tmpImagePath: File): String {
        val ocrImageDir = fileStorage.getOCRImageDir()
        val newFile = File(ocrImageDir, tmpImagePath.name)
        tmpImagePath.copyTo(newFile)
        return ocrImageDir.path
    }
}

private fun String.toFile(): File {
    return File(this)
}