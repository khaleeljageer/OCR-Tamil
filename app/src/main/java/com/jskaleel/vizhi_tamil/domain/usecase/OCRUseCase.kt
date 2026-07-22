package com.jskaleel.vizhi_tamil.domain.usecase

import com.jskaleel.vizhi_tamil.core.model.OCRResult
import com.jskaleel.vizhi_tamil.data.repository.OCRRepository
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface OCRUseCase {
    suspend fun recognizeAndSave(imagePaths: List<String>): OCRResult<ImageOCR>
    suspend fun getScan(id: Int): ImageOCR?
    suspend fun updateScanText(id: Int, text: String)
    fun getRecentScans(): Flow<List<ImageOCR>>
    suspend fun deleteScans(scans: List<ImageOCR>)
}

class OCRUseCaseImpl @Inject constructor(
    private val ocrRepository: OCRRepository,
) : OCRUseCase {
    override suspend fun recognizeAndSave(imagePaths: List<String>): OCRResult<ImageOCR> =
        ocrRepository.recognizeAndSave(imagePaths)

    override suspend fun getScan(id: Int): ImageOCR? = ocrRepository.getScan(id)

    override suspend fun updateScanText(id: Int, text: String) =
        ocrRepository.updateScanText(id, text)

    override fun getRecentScans(): Flow<List<ImageOCR>> = ocrRepository.getRecentScans()

    override suspend fun deleteScans(scans: List<ImageOCR>) = ocrRepository.deleteScans(scans)
}
