package com.jskaleel.vizhi_tamil.data.di

import android.content.Context
import androidx.room.Room
import com.jskaleel.vizhi_tamil.data.source.local.room.VizhiTamilDatabase
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppDatabaseModule {

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

    companion object {
        private const val APP_DATABASE_NAME = "vizhi_tamil.db"
    }
}