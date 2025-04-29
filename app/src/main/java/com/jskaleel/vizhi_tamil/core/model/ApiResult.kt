package com.jskaleel.vizhi_tamil.core.model

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.CancellationException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String) : ApiResult<Nothing>()
}

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        val result = apiCall()
        ApiResult.Success(result)
    } catch (e: ClientRequestException) {
        ApiResult.Error(e.response.status.value, parseErrorMessage(e))
    } catch (e: ServerResponseException) {
        ApiResult.Error(e.response.status.value, parseErrorMessage(e))
    } catch (e: ResponseException) {
        ApiResult.Error(e.response.status.value, parseErrorMessage(e))
    } catch (_: UnknownHostException) {
        ApiResult.Error(null, "No internet connection")
    } catch (_: SocketTimeoutException) {
        ApiResult.Error(null, "Request timed out")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(null, "Unexpected error: ${e.localizedMessage ?: "Unknown error"}")
    }
}

suspend fun parseErrorMessage(e: ResponseException): String {
    return try {
        e.response.bodyAsText()
    } catch (_: Exception) {
        e.message ?: "Unknown error"
    }
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (Int?, String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(code, message)
    return this
}