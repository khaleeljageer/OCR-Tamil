package com.jskaleel.vizhi_tamil.domain.mapper

import com.jskaleel.vizhi_tamil.core.model.ApiResultMapper
import com.jskaleel.vizhi_tamil.core.utils.toRelativeTimeStamp
import com.jskaleel.vizhi_tamil.data.model.ImageOCRResponseDTO
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR

class ImageOCRMapper : ApiResultMapper<ImageOCRResponseDTO, ImageOCR>() {
    override fun onSuccess(input: ImageOCRResponseDTO): ImageOCR {
        return ImageOCR(
            text = input.text,
            accuracy = input.accuracy,
            timeStamp = input.timeStamp.toRelativeTimeStamp(),
            imagePath = input.imagePath
        )
    }

    override fun onError(error: String): String {
        return error
    }
}