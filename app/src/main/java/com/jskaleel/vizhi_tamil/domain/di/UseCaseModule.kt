package com.jskaleel.vizhi_tamil.domain.di

import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCase
import com.jskaleel.vizhi_tamil.domain.usecase.OCRUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun getOCRUseCase(
        useCase: OCRUseCaseImpl,
    ): OCRUseCase
}