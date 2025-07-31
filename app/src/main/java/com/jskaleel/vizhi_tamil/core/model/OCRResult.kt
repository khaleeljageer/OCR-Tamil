package com.jskaleel.vizhi_tamil.core.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class OCRResult<out T> {
    data class Success<T>(val data: T) : OCRResult<T>()
    data class Error(val code: Int? = null, val message: String) : OCRResult<Nothing>()
}

inline fun <I> OCRResult<I>.onSuccess(action: (I) -> Unit): OCRResult<I> {
    if (this is OCRResult.Success) action(data)
    return this
}

inline fun <I> OCRResult<I>.onError(action: (Int?, String?) -> Unit): OCRResult<I> {
    if (this is OCRResult.Error) action(code, message)
    return this
}

suspend fun <I, O> OCRResult<I>.map(responseProvider: ApiResultMapper<I, O>): OCRResult<O> {
    return withContext(Dispatchers.Default) {
        responseProvider.map(this@map)
    }
}
