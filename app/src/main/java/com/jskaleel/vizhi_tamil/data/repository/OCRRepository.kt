package com.jskaleel.vizhi_tamil.data.repository

import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.utils.toRelativeTimeStamp
import com.jskaleel.vizhi_tamil.data.model.ImageOCRResponseDTO
import com.jskaleel.vizhi_tamil.data.source.local.storage.FileStorage
import java.io.File
import javax.inject.Inject

interface OCRRepository {
    fun fetchTextFromImage(imagePath: String): ApiResult<ImageOCRResponseDTO>
}

class OCRRepositoryImpl @Inject constructor(
    private val tessBaseAPI: TessBaseAPI,
    private val fileStorage: FileStorage
) : OCRRepository {
    override fun fetchTextFromImage(imagePath: String): ApiResult<ImageOCRResponseDTO> {
        with(tessBaseAPI) {
            val tessdataPath = fileStorage.getFilesDir().absolutePath
            Log.d("OCRRepositoryImpl", "fetchTextFromImage: $tessdataPath")
            init(tessdataPath, "tamhng+eng")
            setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD)

            setImage(imagePath.toFile())
        }

        val text = try {
            tessBaseAPI.getHOCRText(1)
        } catch (_: Exception) {
            ""
        }
        val accuracy = tessBaseAPI.meanConfidence()
        val timeStamp = System.currentTimeMillis().toRelativeTimeStamp()
        tessBaseAPI.clear()
        return if (text.isNotEmpty()) {
            ApiResult.Success(ImageOCRResponseDTO(text, accuracy, timeStamp, imagePath))
        } else {
            ApiResult.Error(null, "No text found")
        }
    }

    fun stopTesseract() {
        tessBaseAPI.stop()
        tessBaseAPI.recycle()
    }
}

private fun String.toFile(): File {
    return File(this)
}