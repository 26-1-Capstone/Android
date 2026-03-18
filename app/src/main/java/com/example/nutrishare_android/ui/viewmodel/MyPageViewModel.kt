package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: NutriRepository
) : ViewModel() {
    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _participations = MutableStateFlow<List<Participation>>(emptyList())
    val participations: StateFlow<List<Participation>> = _participations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { fetchAll() }

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
                val partJob = launch {
                    repository.getMyParticipations().onSuccess { _participations.value = it }
                }
                profileJob.join(); ordersJob.join(); partJob.join()
            } catch (e: Exception) { /* ignore */ } finally { _isLoading.value = false }
        }
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

    init { fetchProfile() }

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getMyProfile()
                    .onSuccess { _profile.value = it }
            } catch (e: Exception) { /* ignore */ } finally { _isLoading.value = false }
        }
    }

    fun save(nickname: String, address: com.example.nutrishare_android.ui.components.AddressData, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val req = com.example.nutrishare_android.data.model.UpdateProfileRequest(
                    nickname = nickname,
                    zipCode = address.zipcode,
                    addressLine1 = address.basicAddress,
                    addressLine2 = address.detailAddress
                )
                repository.updateMyProfile(req)
                    .onSuccess {
                        _toastMessage.value = "프로필이 성공적으로 수정되었습니다."
                        onSuccess()
                    }
                    .onFailure { _toastMessage.value = "저장에 실패했습니다." }
            } catch (e: Exception) {
                _toastMessage.value = "저장 실패: ${e.message}"
            } finally { _isSaving.value = false }
        }
    }

    fun clearToast() { _toastMessage.value = null }
}
