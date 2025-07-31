package com.jskaleel.vizhi_tamil.core.ocr

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object TrainedDataManager {
    fun scheduleCopyWork(context: Context) {
        val request = OneTimeWorkRequestBuilder<TrainedDataCopyWorker>()
            .setConstraints(Constraints.Builder().build())
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "TrainedDataCopyWork",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
