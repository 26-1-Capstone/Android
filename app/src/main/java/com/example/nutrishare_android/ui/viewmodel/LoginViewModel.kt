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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStorage: AuthStorage
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun loginWithDev(provider: String) {
        // provider is reserved for future OAuth implementation
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.devLogin()
                    .onSuccess { token ->
                        authStorage.setToken(token)
                        _isAuthenticated.value = true
                    }
                    .onFailure { _errorMessage.value = "로그인에 실패했습니다." }
            } catch (e: Exception) {
                _errorMessage.value = "로그인 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
