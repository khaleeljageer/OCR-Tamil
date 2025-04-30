package com.jskaleel.vizhi_tamil.data.source.remote

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.utils.sizeInMBString
import com.jskaleel.vizhi_tamil.data.model.DownloadResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ModelDataSource {
    suspend fun downloadModel(name: String, url: String): Flow<ApiResult<DownloadResponseDTO>>
}
class RemoteModelDataSource(private val httpClient: HttpClient) : ModelDataSource {
    override suspend fun downloadModel(
        name: String,
        url: String
    ): Flow<ApiResult<DownloadResponseDTO>> = flow {
        emit(ApiResult.Success(DownloadResponseDTO(0f, 0, "0 MB")))

        try {
            val response: HttpResponse = httpClient.get(url)
            val channel = response.bodyAsChannel()
            val totalBytes = response.contentLength() ?: 0L
            var bytesDownloaded = 0L
            val buffer = ByteArray(8192)

            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                if (bytesRead == -1) break

                bytesDownloaded += bytesRead

                val progress = (bytesDownloaded.toFloat() / totalBytes).coerceIn(0f, 1f)

                emit(
                    ApiResult.Success(
                        DownloadResponseDTO(
                            progress = progress,
                            bytesDownloaded = bytesDownloaded,
                            fileSize = totalBytes.sizeInMBString(),
                            chunk = buffer.copyOf(bytesRead)
                        )
                    )
                )
            }

            emit(
                ApiResult.Success(
                    DownloadResponseDTO(
                        progress = 1f,
                        bytesDownloaded = bytesDownloaded,
                        fileSize = totalBytes.sizeInMBString(),
                        chunk = null
                    )
                )
            )
        } catch (e: Exception) {
            emit(ApiResult.Error(null, e.message ?: "Unknown error"))
        }
    }
}