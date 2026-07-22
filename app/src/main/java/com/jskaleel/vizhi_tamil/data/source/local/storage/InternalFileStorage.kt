package com.jskaleel.vizhi_tamil.data.source.local.storage

import android.content.Context
import java.io.File

interface FileStorage {
    fun getOCRImageDir(): File
    fun getFilesDir(): File
}

class InternalFileStorage(private val context: Context) : FileStorage {
    override fun getOCRImageDir(): File {
        return File(context.filesDir, "ocr_images").apply {
            if (!exists()) mkdirs()
        }
    }

    override fun getFilesDir(): File {
        return File(context.filesDir.path)
    }
}
