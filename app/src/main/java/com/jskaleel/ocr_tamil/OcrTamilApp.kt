package com.jskaleel.ocr_tamil

import androidx.multidex.MultiDexApplication
import com.jskaleel.ocr_tamil.di.preferenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class OcrTamilApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OcrTamilApp)
            androidLogger(Level.DEBUG)
            modules(preferenceModule)
        }

    }
}