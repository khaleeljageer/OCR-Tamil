package com.jskaleel.ocr_tamil.model

sealed class ConverterResult {
    data class MaxPageError(val message: String) : ConverterResult()
}
