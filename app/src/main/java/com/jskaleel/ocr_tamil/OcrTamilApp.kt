package com.jskaleel.ocr_tamil

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OcrTamilApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

    }
}