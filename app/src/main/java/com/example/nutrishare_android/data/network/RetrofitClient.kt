package com.example.nutrishare_android.data.network

import android.content.Context
import com.example.nutrishare_android.data.local.AuthStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// frontend api.js:
//   baseURL = VITE_API_BASE_URL || '/api/v1'
//   요청 인터셉터: Authorization: Bearer {token}
//   응답 인터셉터: 401 → /api/v1/auth/reissue → 재시도

object RetrofitClient {

    // baseURL은 frontend와 동일하게 /api/v1 경로 기준
    // 에뮬레이터에서 로컬 서버 접근 시 10.0.2.2 사용
    // 실기기나 실서버 사용 시 실제 도메인으로 교체
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    private lateinit var authStorage: AuthStorage
    private var isRefreshing = false

    fun init(context: Context) {
        authStorage = AuthStorage(context)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 요청 인터셉터: Authorization: Bearer {token} 자동 부착 (frontend api.js와 동일)
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val token = authStorage.getToken()

        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        // 401 응답 시 토큰 재발급 후 재시도 (frontend api.js 응답 인터셉터와 동일)
        if (response.code == 401 && !isRefreshing) {
            response.close()
            isRefreshing = true
            try {
                val refreshResponse = chain.proceed(
                    originalRequest.newBuilder()
                        .url("${BASE_URL}auth/reissue")
                        .post(okhttp3.RequestBody.create(null, ByteArray(0)))
                        .header("Authorization", "Bearer $token")
                        .build()
                )

                if (refreshResponse.isSuccessful) {
                    val newToken = refreshResponse.body?.string()
                        ?.let { com.google.gson.Gson().fromJson(it, com.example.nutrishare_android.data.model.ApiResponse::class.java) }
                        ?.data as? String

                    if (newToken != null) {
                        authStorage.setToken(newToken)
                        refreshResponse.close()
                        // 새 토큰으로 원본 요청 재시도
                        return@Interceptor chain.proceed(
                            originalRequest.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                        )
                    }
                }
                refreshResponse.close()
                authStorage.removeToken()
            } finally {
                isRefreshing = false
            }
        }

        response
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
