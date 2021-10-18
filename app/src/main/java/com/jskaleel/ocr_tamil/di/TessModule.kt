package com.jskaleel.ocr_tamil.di

import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object TessModule {

    @Provides
    fun provideTess(fileUtils: FileUtils): TessScanner {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        return TessScanner(path, "eng+tam")
    }
}