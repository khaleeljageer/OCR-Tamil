package com.jskaleel.vizhi_tamil.data.di

import com.jskaleel.vizhi_tamil.data.repository.OCRRepository
import com.jskaleel.vizhi_tamil.data.repository.OCRRepositoryImpl
import com.jskaleel.vizhi_tamil.data.repository.SettingsRepository
import com.jskaleel.vizhi_tamil.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun getOCRRepository(
        repository: OCRRepositoryImpl
    ): OCRRepository

    @Binds
    @Singleton
    abstract fun getSettingsRepository(
        repository: SettingsRepositoryImpl
    ): SettingsRepository
}