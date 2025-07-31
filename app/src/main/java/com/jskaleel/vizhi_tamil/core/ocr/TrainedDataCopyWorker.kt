package com.jskaleel.vizhi_tamil.core.ocr

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.IOException

class TrainedDataCopyWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val languages = listOf("tam", "eng")
        val tessDataDir = File(applicationContext.filesDir, "tessdata")
        if (!tessDataDir.exists()) tessDataDir.mkdirs()

        for (lang in languages) {
            val trainedDataFile = File(tessDataDir, "$lang.traineddata")
            if (!trainedDataFile.exists()) {
                try {
                    applicationContext.assets.open("tessdata/$lang.traineddata").use { input ->
                        trainedDataFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: IOException) {
                    return Result.failure()
                }
            }
        }
        return Result.success()
    }
}