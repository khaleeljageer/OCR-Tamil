package com.jskaleel.ocr_tamil.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import timber.log.Timber
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

    fun scanForPDF(): MutableList<LocalFiles> {
        val localFiles: MutableList<LocalFiles> = mutableListOf()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )

        val mimeType = "application/pdf"
        val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeType + "')"
//        val whereClause = "_data LIKE '%.jpeg'"
        val orderBy = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        context.contentResolver.query(
            collection,
            projection,
            whereClause,
            null,
            orderBy
        )?.use { cursor ->
            {
                Timber.d("Cursor : " + cursor.count)
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val modifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val nameCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
//                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                if (cursor.moveToFirst()) {
                    do {
                        val fileUri: Uri = Uri.withAppendedPath(
                            MediaStore.Files.getContentUri("external"),
                            cursor.getString(idCol)
                        )
//                        val mimeType = cursor.getString(mimeCol)
//                        val dateAdded = cursor.getLong(addedCol)
//                        val dateModified = cursor.getLong(modifiedCol)
                        val size = cursor.getLong(sizeCol)
                        val fileName = cursor.getString(nameCol)
//                        val title = cursor.getString(titleCol)
                        localFiles.add(
                            LocalFiles(
                                idCol,
                                fileName,
//                                mimeType,
//                                dateAdded,
                                fileUri,
//                                title,
                                size
                            )
                        )
                        Timber.d("Cursor : $fileName")
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
        }

        return localFiles
    }
}

data class LocalFiles(
    val id: Int,
    val fileName: String,
//    val mimeType: String,
//    val dateAdded: Long,
    val fileUri: Uri,
//    val titleCol: String,
    val size: Long
)