package com.jskaleel.vizhi_tamil.core.model

interface IMapper<I, O> {
    fun map(input: I): O
}

abstract class ApiResultMapper<I, O> : IMapper<OCRResult<I>, OCRResult<O>> {

    abstract fun onSuccess(input: I): O

    abstract fun onError(error: String): String

    override fun map(input: OCRResult<I>): OCRResult<O> {
        return when (input) {
            is OCRResult.Success -> {
                try {
                    OCRResult.Success(onSuccess(input.data))
                } catch (e: Error) {
                    OCRResult.Error(null, e.message.toString())
                }
            }

            is OCRResult.Error -> {
                OCRResult.Error(null, onError(input.message))
            }
        }
    }
}