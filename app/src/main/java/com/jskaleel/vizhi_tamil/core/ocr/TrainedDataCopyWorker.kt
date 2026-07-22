package com.jskaleel.vizhi_tamil.core.ocr

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Eagerly pre-warms the traineddata copy on app start so the first scan is fast.
 * The actual copy lives in [TrainedDataInstaller]; the repository performs the
 * same install lazily, so OCR still works even if this pre-warm never runs.
 */
class TrainedDataCopyWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return TrainedDataInstaller(applicationContext).installBlocking().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
    }
}
