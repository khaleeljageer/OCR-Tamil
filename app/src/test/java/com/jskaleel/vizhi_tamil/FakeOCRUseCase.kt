package com.jskaleel.vizhi_tamil

import com.jskaleel.vizhi_tamil.core.model.OCRResult
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory OCRUseCase for ViewModel tests; records interactions. */
class FakeOCRUseCase(
    initialScans: List<ImageOCR> = emptyList(),
) : OCRUseCase {

    val scans = MutableStateFlow(initialScans)
    val deleted = mutableListOf<List<ImageOCR>>()
    var recognizeResult: OCRResult<ImageOCR> =
        OCRResult.Success(ImageOCR("text", 90, "now", "/a.jpg", id = 42))
    var lastRecognizedPaths: List<String>? = null

    override suspend fun recognizeAndSave(imagePaths: List<String>): OCRResult<ImageOCR> {
        lastRecognizedPaths = imagePaths
        return recognizeResult
    }

    override suspend fun getScan(id: Int): ImageOCR? = scans.value.firstOrNull { it.id == id }

    override suspend fun updateScanText(id: Int, text: String) {
        scans.value = scans.value.map { if (it.id == id) it.copy(text = text) else it }
    }

    override fun getRecentScans(): Flow<List<ImageOCR>> = scans

    override suspend fun deleteScans(scans: List<ImageOCR>) {
        deleted += scans
        this.scans.value = this.scans.value.filterNot { it in scans }
    }
}
