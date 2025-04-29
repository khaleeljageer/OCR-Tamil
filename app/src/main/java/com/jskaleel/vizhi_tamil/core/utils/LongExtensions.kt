package com.jskaleel.vizhi_tamil.core.utils

import android.annotation.SuppressLint


@SuppressLint("DefaultLocale")
fun Long?.sizeInMBString(): String {
    val sizeInMB = if (this != null) {
        this.toDouble() / (1024 * 1024)
    } else {
        0
    }
    return String.format("%.2f MB", sizeInMB)
}