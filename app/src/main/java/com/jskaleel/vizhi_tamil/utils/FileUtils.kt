package com.jskaleel.vizhi_tamil.utils

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FileUtils @Inject constructor(private val context: Context) {

    fun getAppFileDir(): File? {
        return context.getExternalFilesDir(Constants.TESS_DATA_PATH)
    }

    fun getPdfFileDir(): File? {
        return context.getExternalFilesDir(Constants.DOCUMENT_PDF_PATH)
    }

    fun getTessDataPath(): File? {
        return context.getExternalFilesDir(Constants.TESS_FAST_DATA_PATH)
    }
}