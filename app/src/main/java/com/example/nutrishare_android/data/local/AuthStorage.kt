package com.example.nutrishare_android.data.local

import android.content.Context
import android.content.SharedPreferences

class AuthStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nutrishare_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN_KEY = "nutrishare_access_token"
        private const val GUEST_MODE_KEY = "nutrishare_guest_mode"
    }

    fun getToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)

    fun setToken(token: String) {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, token)
            .putBoolean(GUEST_MODE_KEY, false)
            .apply()
    }

    fun removeToken() {
        prefs.edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    fun isAuthenticated(): Boolean = !getToken().isNullOrEmpty()

    fun isGuestMode(): Boolean = prefs.getBoolean(GUEST_MODE_KEY, false)

    fun enableGuestMode() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .putBoolean(GUEST_MODE_KEY, true)
            .apply()
    }

    fun disableGuestMode() {
        prefs.edit().putBoolean(GUEST_MODE_KEY, false).apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .putBoolean(GUEST_MODE_KEY, false)
            .apply()
    }
}
