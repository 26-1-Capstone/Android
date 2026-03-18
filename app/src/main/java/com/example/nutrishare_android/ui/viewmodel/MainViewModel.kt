package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false
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
            if (!authStorage.isAuthenticated()) {
                _authState.value = AuthState(isLoading = false, isAuthenticated = false)
                return@launch
            }

            authRepository.reissueToken()
                .onSuccess { token ->
                    authStorage.setToken(token)
                    _authState.value = AuthState(isLoading = false, isAuthenticated = true)
                }
                .onFailure {
                    authStorage.removeToken()
                    _authState.value = AuthState(isLoading = false, isAuthenticated = false)
                }
        }
    }
}
