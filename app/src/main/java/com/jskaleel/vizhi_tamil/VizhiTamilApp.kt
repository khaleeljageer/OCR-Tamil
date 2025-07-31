package com.jskaleel.vizhi_tamil

import android.app.Application
import com.jskaleel.vizhi_tamil.core.ocr.TrainedDataManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VizhiTamilApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TrainedDataManager.scheduleCopyWork(this)
    }
}