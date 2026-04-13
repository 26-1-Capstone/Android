package com.example.nutrishare_android.data.network

import android.content.Context
import android.webkit.CookieManager
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.model.ApiResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private lateinit var authStorage: AuthStorage
    private lateinit var cookieManager: CookieManager
    private var isRefreshing = false
    private val gson = Gson()
    private val tokenResponseType = object : TypeToken<ApiResponse<String>>() {}.type

    fun init(context: Context) {
        authStorage = AuthStorage(context)
        cookieManager = CookieManager.getInstance().apply {
            setAcceptCookie(true)
            flush()
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = authStorage.getToken()

        val request = if (token.isNullOrBlank()) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)
        if (response.code != 401 || isRefreshing || token.isNullOrBlank()) {
            return@Interceptor response
        }

        isRefreshing = true

        try {
            val refreshRequest = originalRequest.newBuilder()
                .url("${NetworkConfig.API_BASE_URL}auth/reissue")
                .post(ByteArray(0).toRequestBody(null))
                .header("Authorization", "Bearer $token")
                .build()

            val refreshResponse = chain.proceed(refreshRequest)
            val newToken = refreshResponse.body?.string()
                ?.let { body -> gson.fromJson<ApiResponse<String>>(body, tokenResponseType) }
                ?.data

            if (refreshResponse.isSuccessful && !newToken.isNullOrBlank()) {
                authStorage.setToken(newToken)
                refreshResponse.close()
                response.close()
                return@Interceptor chain.proceed(
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                )
            }

            refreshResponse.close()
            authStorage.removeToken()
            response
        } finally {
            isRefreshing = false
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(WebViewCookieJar(cookieManager))
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
