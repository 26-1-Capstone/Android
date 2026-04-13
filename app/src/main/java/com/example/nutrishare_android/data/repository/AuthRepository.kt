package com.example.nutrishare_android.data.repository

interface AuthRepository {
    fun getKakaoLoginUrl(redirect: String? = null): String
    suspend fun completeOAuthLogin(accessToken: String): Result<Unit>
    suspend fun devLogin(): Result<String>
    suspend fun reissueToken(): Result<String>
}
