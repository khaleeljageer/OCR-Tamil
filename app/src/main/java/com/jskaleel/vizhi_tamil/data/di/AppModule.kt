package com.jskaleel.vizhi_tamil.data.di

import android.content.Context
import androidx.room.Room
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.data.source.local.room.VizhiTamilDatabase
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.data.source.local.storage.FileStorage
import com.jskaleel.vizhi_tamil.data.source.local.storage.InternalFileStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): VizhiTamilDatabase {
        return Room.databaseBuilder(
            context,
            VizhiTamilDatabase::class.java,
            APP_DATABASE_NAME,
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    @Singleton
    fun getRecentScanDao(appDatabase: VizhiTamilDatabase): RecentScanDao =
        appDatabase.recentScanDao()

    @Provides
    @Singleton
    fun provideFileStorage(@ApplicationContext context: Context): FileStorage {
        return InternalFileStorage(context)
    }

    @Provides
    @Singleton
    fun provideTessApi(): TessBaseAPI {
        return TessBaseAPI()
    }

    companion object {
        private const val APP_DATABASE_NAME = "vizhi_tamil.db"
    }
}