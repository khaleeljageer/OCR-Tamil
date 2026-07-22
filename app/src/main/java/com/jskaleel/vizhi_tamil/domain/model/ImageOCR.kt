package com.jskaleel.vizhi_tamil.domain.model

data class ImageOCR(
    val text: String,
    val accuracy: Int,
    val timeStamp: String,
    val imagePath: String,
    // Row id from persistence; 0 for a freshly recognised (not-yet-saved) result.
    val id: Int = 0,
)
