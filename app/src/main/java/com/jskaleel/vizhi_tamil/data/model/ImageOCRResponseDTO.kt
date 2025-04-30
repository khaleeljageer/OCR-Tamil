package com.jskaleel.vizhi_tamil.data.model

import com.jskaleel.vizhi_tamil.domain.model.ImageOCR

data class ImageOCRResponseDTO(
    val text: String,
    val accuracy: Int,
    val timeStamp: String,
    val imagePath: String
) {
    fun toDomain(): ImageOCR {
        return ImageOCR(
            text = text,
            accuracy = accuracy,
            timeStamp = timeStamp,
            imagePath = imagePath
        )
    }
}