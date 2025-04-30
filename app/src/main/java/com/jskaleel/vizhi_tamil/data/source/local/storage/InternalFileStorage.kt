package com.jskaleel.vizhi_tamil.data.source.local.storage

import android.content.Context
import java.io.File

interface FileStorage {
    fun saveFile(fileName: String, content: ByteArray): Result<File>
    fun getTessDataDir(): File
    fun getFilesDir(): File
}

class InternalFileStorage(private val context: Context) : FileStorage {
    override fun saveFile(fileName: String, content: ByteArray): Result<File> {
        return try {
            val tessDataDir = getTessDataDir()
            val file = File(tessDataDir, fileName)
            file.writeBytes(content)
            if (file.exists() && file.length() > 0) {
                Result.success(file)
            } else {
                Result.failure(Exception("Failed to save $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTessDataDir(): File {
        return File(context.filesDir, "tessdata").apply {
            if (!exists()) mkdirs()
        }
    }

    override fun getFilesDir(): File {
        return File(context.filesDir.path)
    }
}