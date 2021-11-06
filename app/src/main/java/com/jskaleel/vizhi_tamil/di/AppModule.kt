package com.jskaleel.vizhi_tamil.di

import android.content.Context
import androidx.room.Room
import com.jskaleel.vizhi_tamil.BuildConfig
import com.jskaleel.vizhi_tamil.db.VizhiDatabase
import com.jskaleel.vizhi_tamil.db.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.ui.main.RecentScanAdapter
import com.jskaleel.vizhi_tamil.utils.FileUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttp.addInterceptor(interceptor)
        }
        return okHttp.build()
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
    fun provideScanAdapter(): RecentScanAdapter {
        return RecentScanAdapter(mutableListOf())
    }
}