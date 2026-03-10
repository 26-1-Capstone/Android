package com.example.nutrishare_android.di

import com.example.nutrishare_android.data.repository.AuthRepository
import com.example.nutrishare_android.data.repository.AuthRepositoryImpl
import com.example.nutrishare_android.data.repository.NutriRepository
import com.example.nutrishare_android.data.repository.NutriRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindNutriRepository(impl: NutriRepositoryImpl): NutriRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
