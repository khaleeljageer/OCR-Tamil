package com.jskaleel.vizhi_tamil.data.repository

import com.jskaleel.vizhi_tamil.core.model.ApiResult
import com.jskaleel.vizhi_tamil.core.model.CONFIG_URL
import com.jskaleel.vizhi_tamil.core.model.safeApiCall
import com.jskaleel.vizhi_tamil.data.model.ConfigResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

interface SetupRepository {
    suspend fun downloadModel()
    suspend fun downloadConfig(): ApiResult<ConfigResponse>
}

class SetupRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : SetupRepository {
    override suspend fun downloadModel() {

    }

    override suspend fun downloadConfig(): ApiResult<ConfigResponse> {
        return safeApiCall {
            httpClient.get(CONFIG_URL).body<ConfigResponse>()
        }
    }
}