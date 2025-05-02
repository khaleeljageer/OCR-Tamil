package com.jskaleel.vizhi_tamil.domain.usecase

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.model.map
import com.jskaleel.vizhi_tamil.core.model.onSuccess
import com.jskaleel.vizhi_tamil.data.repository.OCRRepository
import com.jskaleel.vizhi_tamil.domain.mapper.ImageOCRMapper
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface OCRUseCase {
    suspend fun fetchTextFromImage(imagePath: String): ApiResult<ImageOCR>
    fun getRecentScans(): Flow<List<ImageOCR>>
}

class OCRUseCaseImpl @Inject constructor(
    private val ocrRepository: OCRRepository
) : OCRUseCase {
    override suspend fun fetchTextFromImage(imagePath: String): ApiResult<ImageOCR> {
        return ocrRepository.fetchTextFromImage(imagePath)
            .onSuccess {
                ocrRepository.saveImageResult(it)
            }
            .map(ImageOCRMapper())
    }

    override fun getRecentScans(): Flow<List<ImageOCR>> {
        return ocrRepository.getRecentScans()
    }
}