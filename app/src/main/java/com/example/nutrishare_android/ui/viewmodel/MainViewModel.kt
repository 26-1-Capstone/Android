package com.example.nutrishare_android.ui.viewmodel

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
                MockDataConfig.forceMock = false
                _authState.value = AuthState(isLoading = false, isAuthenticated = false, isGuestMode = false)
                return@launch
            }

            MockDataConfig.forceMock = false
            authRepository.reissueToken()
                .onSuccess { token ->
                    authStorage.setToken(token)
                    _authState.value = AuthState(isLoading = false, isAuthenticated = true, isGuestMode = false)
                }
                .onFailure {
                    authStorage.clearSession()
                    _authState.value = AuthState(isLoading = false, isAuthenticated = false, isGuestMode = false)
                }
        }
    }
}
