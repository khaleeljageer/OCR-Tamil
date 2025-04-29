package com.jskaleel.vizhi_tamil.data.repository

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.model.safeApiCall
import com.jskaleel.vizhi_tamil.core.utils.sizeInMBString
import com.jskaleel.vizhi_tamil.data.model.ConfigResponse
import com.jskaleel.vizhi_tamil.data.model.DownloadResponse
import com.jskaleel.vizhi_tamil.data.source.local.storage.FileStorage
import com.jskaleel.vizhi_tamil.data.source.remote.ModelDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SetupRepository {
    suspend fun downloadModel(): Flow<ApiResult<DownloadResponse>>
    suspend fun downloadConfig(): ApiResult<ConfigResponse>
}

class SetupRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val modelDataSource: ModelDataSource,
    private val fileStorage: FileStorage
) : SetupRepository {

    override suspend fun downloadModel(): Flow<ApiResult<DownloadResponse>> = flow {
        val modelUrls = mapOf(
            "Tamil" to "https://github.com/khaleeljageer/tesseract-gt-builder/raw/refs/heads/main/model/27-04-25/tamhng.traineddata",
            "English" to "https://github.com/tesseract-ocr/tessdata_fast/raw/refs/heads/main/eng.traineddata"
        )

        val totalFiles = modelUrls.size
        val completedFlags = mutableMapOf<String, Boolean>()

        modelUrls.forEach { (lang, url) ->
            val output = mutableListOf<Byte>()

            modelDataSource.downloadModel(lang, url)
                .map { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val res = result.data
                            res.chunk?.let { output.addAll(it.toList()) }

                            if (res.progress == 1f) {
                                val fileName = url.substringAfterLast("/")
                                val fileResult =
                                    fileStorage.saveFile(fileName, output.toByteArray())

                                fileResult.fold(
                                    onSuccess = {
                                        completedFlags[lang] = true
                                        val allCompleted =
                                            completedFlags.size == totalFiles && completedFlags.all { it.value }
                                        ApiResult.Success(
                                            DownloadResponse(
                                                progress = 1f,
                                                bytesDownloaded = it.length(),
                                                fileSize = it.length().sizeInMBString(),
                                                fileCompleted = allCompleted,
                                            )
                                        )
                                    },
                                    onFailure = {
                                        ApiResult.Error(null, it.message ?: "Failed to save file")
                                    }
                                )
                            } else {
                                ApiResult.Success(
                                    DownloadResponse(
                                        progress = res.progress,
                                        bytesDownloaded = res.bytesDownloaded,
                                        fileSize = res.fileSize,
                                        fileCompleted = false,
                                    )
                                )
                            }
                        }

                        else -> result
                    }
                }.collect { emit(it) }
        }
    }

    override suspend fun downloadConfig(): ApiResult<ConfigResponse> {
        return safeApiCall {
            httpClient.get(CONFIG_URL).body<ConfigResponse>()
        }
    }

    companion object {

        const val CONFIG_URL =
            "https://github.com/khaleeljageer/OCR-Tamil/raw/refs/heads/main/config.json"
    }
}