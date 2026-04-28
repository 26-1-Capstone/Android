package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.network.ApiService
import com.example.nutrishare_android.data.network.NetworkConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val authStorage: AuthStorage
) : AuthRepository {
    private suspend fun <T> withMock(
        mock: () -> Result<T>,
        apiCall: suspend () -> Result<T>
    ): Result<T> {
        if (MockDataConfig.forceMock) return mock()
        return apiCall()
    }

    override fun getKakaoLoginUrl(redirect: String?): String {
        if (redirect.isNullOrBlank()) {
            return NetworkConfig.KAKAO_LOGIN_URL
        }

        val encodedRedirect = URLEncoder.encode(redirect, StandardCharsets.UTF_8.toString())
        return "${NetworkConfig.KAKAO_LOGIN_URL}?redirect=$encodedRedirect"
    }

    override suspend fun completeOAuthLogin(accessToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            authStorage.setToken(accessToken)
            Result.success(Unit)
        }
    }

    override suspend fun devLogin(): Result<String> {
        return withMock(
            mock = { MockData.token() },
            apiCall = { api.devLogin().toResult() }
        )
    }

    override suspend fun reissueToken(): Result<String> {
        return withMock(
            mock = { MockData.token() },
            apiCall = { api.reissueToken().toResult() }
        )
    }
}
