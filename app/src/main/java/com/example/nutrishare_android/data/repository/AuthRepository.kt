package com.example.nutrishare_android.data.repository

interface AuthRepository {
    suspend fun devLogin(): Result<String>
    suspend fun reissueToken(): Result<String>
}
