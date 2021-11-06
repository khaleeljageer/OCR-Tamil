package com.jskaleel.vizhi_tamil.model

sealed class ConverterResult {
    data class MaxPageError(val message: String) : ConverterResult()
}
