package com.jskaleel.vizhi_tamil.domain.usecase

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.model.map
import com.jskaleel.vizhi_tamil.data.repository.SetupRepository
import com.jskaleel.vizhi_tamil.domain.mapper.ConfigMapper
import com.jskaleel.vizhi_tamil.domain.model.DownloadProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SetupUseCase {
    suspend fun downloadModel(): Flow<ApiResult<DownloadProgress>>
    suspend fun checkForModelUpdate(): ApiResult<Boolean>
    suspend fun checkModelExists(): ApiResult<Boolean>
}

class SetupUseCaseImpl @Inject constructor(
    private val setupRepository: SetupRepository
) : SetupUseCase {
    override suspend fun downloadModel(): Flow<ApiResult<DownloadProgress>> {
        return setupRepository.downloadModel().map { result ->
            when (result) {
                is ApiResult.Success -> {
                    val input = result.data
                    ApiResult.Success(
                        DownloadProgress(
                            progress = input.progress,
                            bytesDownloaded = input.bytesDownloaded,
                            fileSize = input.fileSize,
                            fileCompleted = input.fileCompleted,
                        )
                    )
                }

                is ApiResult.Error -> ApiResult.Error(result.code, result.message)
            }
        }
    }

    override suspend fun checkForModelUpdate(): ApiResult<Boolean> {
        return setupRepository.downloadConfig().map(ConfigMapper())
    }

    override suspend fun checkModelExists(): ApiResult<Boolean> {
        return setupRepository.checkModelExists()
    }
}