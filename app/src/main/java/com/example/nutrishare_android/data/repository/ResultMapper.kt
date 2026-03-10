package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.model.ApiResponse
import retrofit2.HttpException
import retrofit2.Response

internal fun <T> Response<ApiResponse<T>>.toResult(): Result<T> {
    return if (isSuccessful) {
        val body = body()
        val data = body?.data
        if (data != null) {
            Result.success(data)
        } else {
            Result.failure(IllegalStateException(body?.message ?: "Empty response data"))
        }
    } else {
        Result.failure(HttpException(this))
    }
}

internal fun Response<ApiResponse<Unit>>.toUnitResult(): Result<Unit> {
    return if (isSuccessful) {
        Result.success(Unit)
    } else {
        Result.failure(HttpException(this))
    }
}
