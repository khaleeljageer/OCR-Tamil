package com.jskaleel.ocr_tamil.utils


fun Long.toMB(): String {
    val size = this / 1048576L
    return "$size MB"
}