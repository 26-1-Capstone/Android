package com.example.nutrishare_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.repository.AuthRepository
import com.example.nutrishare_android.data.repository.MockDataConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val isGuestMode: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStorage: AuthStorage
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        validateToken()
    }

    private fun validateToken() {
        viewModelScope.launch {
            if (authStorage.isGuestMode()) {
                Log.d("LoginFlow", "validateToken guest mode detected")
                authStorage.clearSession()
                MockDataConfig.forceMock = false
                _authState.value = AuthState(
                    isLoading = false,
                    isAuthenticated = false,
                    isGuestMode = false
                )
                return@launch
            }

            if (!authStorage.isAuthenticated()) {
                Log.d("LoginFlow", "validateToken no access token")
                MockDataConfig.forceMock = false
                _authState.value = AuthState(isLoading = false, isAuthenticated = false, isGuestMode = false)
                return@launch
            }

            Log.d("LoginFlow", "validateToken trying reissue")
            MockDataConfig.forceMock = false
            authRepository.reissueToken()
                .onSuccess { token ->
                    Log.d("LoginFlow", "reissueToken success tokenLength=${token.length}")
                    authStorage.setToken(token)
                    _authState.value = AuthState(isLoading = false, isAuthenticated = true, isGuestMode = false)
                }
                .onFailure { throwable ->
                    Log.e("LoginFlow", "reissueToken failed", throwable)
                    authStorage.clearSession()
                    _authState.value = AuthState(isLoading = false, isAuthenticated = false, isGuestMode = false)
                }
        }
    }
}
