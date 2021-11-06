package com.jskaleel.vizhi_tamil.utils

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI

class TessScanner constructor(
    tessDataPath: String,
    tessLang: String
) {

    private var tessBaseAPI: TessBaseAPI = TessBaseAPI()

    init {
        tessBaseAPI.init(tessDataPath, tessLang)
    }

    fun getTextFromImage(bitmap: Bitmap?): String {
        tessBaseAPI.setImage(bitmap)
        val textOnImage = try {
            tessBaseAPI.getHOCRText(1)
        } catch (e: Exception) {
            return "Scan Failed"
        }
        return if (textOnImage.isEmpty()) {
            "Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!"
        } else textOnImage
    }

    fun stop() {
        tessBaseAPI.stop()
        tessBaseAPI.recycle()
    }

    fun accuracy(): Int = tessBaseAPI.meanConfidence()

    fun clearLastImage() {
        tessBaseAPI.clear()
    }
}