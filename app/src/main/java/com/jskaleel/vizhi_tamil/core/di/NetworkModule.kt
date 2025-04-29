package com.jskaleel.vizhi_tamil.core.di

import com.jskaleel.vizhi_tamil.data.source.remote.ModelDataSource
import com.jskaleel.vizhi_tamil.data.source.remote.RemoteModelDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = false
                        isLenient = true
                    }, contentType = ContentType.Any
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideModelDataSource(httpClient: HttpClient): ModelDataSource {
        return RemoteModelDataSource(httpClient)
    }
}
