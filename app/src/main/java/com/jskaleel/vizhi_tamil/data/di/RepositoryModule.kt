package com.jskaleel.vizhi_tamil.data.di

import com.jskaleel.vizhi_tamil.data.repository.SetupRepository
import com.jskaleel.vizhi_tamil.data.repository.SetupRepositoryImpl
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
    abstract fun getSetupRepository(
        setupRepository: SetupRepositoryImpl,
    ): SetupRepository
}