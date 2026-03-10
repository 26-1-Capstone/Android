package com.example.nutrishare_android.di

import android.content.Context
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.network.ApiService
import com.example.nutrishare_android.data.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.instance

    @Provides
    @Singleton
    fun provideAuthStorage(@ApplicationContext context: Context): AuthStorage {
        return AuthStorage(context)
    }
}
