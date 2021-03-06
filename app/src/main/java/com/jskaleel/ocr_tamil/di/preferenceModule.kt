package com.jskaleel.ocr_tamil.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jskaleel.ocr_tamil.utils.AppPreference
import com.jskaleel.ocr_tamil.utils.FileUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferenceModule = module {
    single { AppPreference(getSharedPref(androidApplication())) }
    single { FileUtils(androidContext()) }
}

fun getSharedPref(application: Application): SharedPreferences {
    return application.getSharedPreferences("ocr_tamil", Context.MODE_PRIVATE)
}
