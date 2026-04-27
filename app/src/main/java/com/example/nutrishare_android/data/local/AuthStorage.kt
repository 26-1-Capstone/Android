package com.example.nutrishare_android.data.local

import android.content.Context
import android.content.SharedPreferences

// frontend: auth.js — localStorage('nutrishare_access_token') 동일 키 사용
class AuthStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nutrishare_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN_KEY = "nutrishare_access_token"
    }

    fun getToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)

    fun setToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun removeToken() {
        prefs.edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    fun isAuthenticated(): Boolean = !getToken().isNullOrEmpty()
}
