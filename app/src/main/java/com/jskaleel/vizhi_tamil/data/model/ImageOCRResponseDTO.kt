package com.jskaleel.vizhi_tamil.data.model

data class ImageOCRResponseDTO(
    val text: String,
    val accuracy: Int,
    val timeStamp: Long,
    val imagePath: String
)