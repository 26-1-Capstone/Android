package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.model.Order
import com.example.nutrishare_android.data.model.Participation
import com.example.nutrishare_android.data.model.User
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: NutriRepository,
    private val authStorage: AuthStorage
) : ViewModel() {
    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _participations = MutableStateFlow<List<Participation>>(emptyList())
    val participations: StateFlow<List<Participation>> = _participations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    init {
        fetchAll()
    }

    fun refreshOnStart() {
        fetchAll()
    }

    fun fetchAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profileJob = launch {
                    repository.getMyProfile().onSuccess { _profile.value = it }
                }
                val ordersJob = launch {
                    repository.getMyOrders().onSuccess { _orders.value = it }
                }
                val participationsJob = launch {
                    repository.getMyParticipations().onSuccess { _participations.value = it }
                }
                profileJob.join()
                ordersJob.join()
                participationsJob.join()
            } catch (_: Exception) {
                // Keep the current state when refresh fails.
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        if (_isDeletingAccount.value) return

        viewModelScope.launch {
            _isDeletingAccount.value = true
            repository.deleteMyAccount()
                .onSuccess {
                    authStorage.clearSession()
                    onSuccess()
                }
                .onFailure {
                    _toastMessage.value = "Account deletion failed. Please check server support."
                }
            _isDeletingAccount.value = false
        }
    }

    fun logout() {
        authStorage.clearSession()
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {
    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getMyProfile().onSuccess { _profile.value = it }
            } catch (_: Exception) {
                // Ignore and keep any previously shown state.
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun save(
        nickname: String,
        address: com.example.nutrishare_android.ui.components.AddressData,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val request = com.example.nutrishare_android.data.model.UpdateProfileRequest(
                    nickname = nickname,
                    zipCode = address.zipcode,
                    addressLine1 = address.basicAddress,
                    addressLine2 = address.detailAddress
                )
                repository.updateMyProfile(request)
                    .onSuccess {
                        _toastMessage.value = "Profile saved."
                        onSuccess()
                    }
                    .onFailure {
                        _toastMessage.value = "Save failed."
                    }
            } catch (e: Exception) {
                _toastMessage.value = "Save failed: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
