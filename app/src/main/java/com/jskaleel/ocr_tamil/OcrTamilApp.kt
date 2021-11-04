package com.jskaleel.ocr_tamil

import androidx.multidex.MultiDexApplication
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class OcrTamilApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}