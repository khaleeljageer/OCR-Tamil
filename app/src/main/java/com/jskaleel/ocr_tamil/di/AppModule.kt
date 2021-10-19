package com.jskaleel.ocr_tamil.di

import android.content.Context
import androidx.room.Room
import com.jskaleel.ocr_tamil.db.VizhiDatabase
import com.jskaleel.ocr_tamil.db.dao.RecentScanDao
import com.jskaleel.ocr_tamil.ui.main.RecentScanAdapter
import com.jskaleel.ocr_tamil.utils.FileUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFileUtils(@ApplicationContext context: Context): FileUtils {
        return FileUtils(context)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): VizhiDatabase {
        return Room.databaseBuilder(context, VizhiDatabase::class.java, "vizhi_tamil.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRecentDao(vizhiDatabase: VizhiDatabase): RecentScanDao {
        return vizhiDatabase.recentScanDao()
    }

    @Provides
    @Singleton
    fun provideScanAdapter(scanDao: RecentScanDao): RecentScanAdapter {
        return RecentScanAdapter(mutableListOf(), scanDao)
    }
}