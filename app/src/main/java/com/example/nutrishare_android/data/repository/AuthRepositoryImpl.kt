package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AuthRepository {
    private suspend fun <T> withMock(
        mock: () -> Result<T>,
        apiCall: suspend () -> Result<T>
    ): Result<T> {
        if (MockDataConfig.forceMock) return mock()
        return apiCall()
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
