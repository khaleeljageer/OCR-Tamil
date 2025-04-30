package com.jskaleel.vizhi_tamil.core.model


interface IMapper<I, O> {
    fun map(input: I): O
}

abstract class ApiResultMapper<I, O> : IMapper<ApiResult<I>, ApiResult<O>> {

    abstract fun onSuccess(input: I): O

    abstract fun onError(error: String): String

    override fun map(input: ApiResult<I>): ApiResult<O> {
        return when (input) {
            is ApiResult.Success -> {
                try {
                    ApiResult.Success(onSuccess(input.data))
                } catch (e: Error) {
                    ApiResult.Error(null, e.message.toString())
                }
            }

            is ApiResult.Error -> {
                ApiResult.Error(null, onError(input.message))
            }
        }
    }
}