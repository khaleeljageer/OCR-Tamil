package com.jskaleel.vizhi_tamil.domain.di

import com.jskaleel.vizhi_tamil.domain.usecase.SetupUseCase
import com.jskaleel.vizhi_tamil.domain.usecase.SetupUseCaseImpl
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
    abstract fun getSetupUseCase(
        setupUseCase: SetupUseCaseImpl,
    ): SetupUseCase
}