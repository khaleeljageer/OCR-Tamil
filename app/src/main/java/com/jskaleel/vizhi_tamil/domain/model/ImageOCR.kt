package com.jskaleel.vizhi_tamil.domain.model

data class ImageOCR(
    val text: String,
    val accuracy: Int,
    val timeStamp: String,
    val imagePath: String
)
