package com.jskaleel.vizhi_tamil.domain.usecase

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.data.model.ConfigResponse
import com.jskaleel.vizhi_tamil.data.repository.SetupRepository
import javax.inject.Inject

interface SetupUseCase {
    suspend fun downloadModel()
    suspend fun checkForModelUpdate(): ApiResult<ConfigResponse>
}

class SetupUseCaseImpl @Inject constructor(
    private val setupRepository: SetupRepository
) : SetupUseCase {
    override suspend fun downloadModel() {

    }

    override suspend fun checkForModelUpdate(): ApiResult<ConfigResponse> {
        return setupRepository.downloadConfig()
    }
}