package com.example.nutrishare_android.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthSessionState(
    val isAuthenticated: Boolean = false,
    val isGuestMode: Boolean = false
) {
    val canAccessProtectedRoutes: Boolean
        get() = isAuthenticated || isGuestMode
}

class AuthStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nutrishare_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN_KEY = "nutrishare_access_token"
        private const val GUEST_MODE_KEY = "nutrishare_guest_mode"

        private val _sessionState = MutableStateFlow(AuthSessionState())
        val sessionState: StateFlow<AuthSessionState> = _sessionState.asStateFlow()
    }

    init {
        publishSessionState()
    }

    fun getToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)

    fun setToken(token: String) {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, token)
            .putBoolean(GUEST_MODE_KEY, false)
            .apply()
        publishSessionState()
    }

    fun removeToken() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .putBoolean(GUEST_MODE_KEY, false)
            .apply()
        publishSessionState()
    }

    fun isAuthenticated(): Boolean = !getToken().isNullOrEmpty()

    fun isGuestMode(): Boolean = prefs.getBoolean(GUEST_MODE_KEY, false)

    fun enableGuestMode() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .putBoolean(GUEST_MODE_KEY, true)
            .apply()
        publishSessionState()
    }

    fun disableGuestMode() {
        prefs.edit().putBoolean(GUEST_MODE_KEY, false).apply()
        publishSessionState()
    }

    fun clearSession() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .putBoolean(GUEST_MODE_KEY, false)
            .apply()
        publishSessionState()
    }

    private fun publishSessionState() {
        _sessionState.value = AuthSessionState(
            isAuthenticated = isAuthenticated(),
            isGuestMode = isGuestMode()
        )
    }
}
