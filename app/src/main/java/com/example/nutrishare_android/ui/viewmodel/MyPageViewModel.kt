package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.Order
import com.example.nutrishare_android.data.model.Participation
import com.example.nutrishare_android.data.model.User
import com.example.nutrishare_android.data.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyPageViewModel : ViewModel() {
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
                // frontend: Promise.all과 동일하게 병렬 호출
                val profileJob = launch { 
                    RetrofitClient.instance.getMyProfile().body()?.data?.let { _profile.value = it }
                }
                val ordersJob = launch {
                    RetrofitClient.instance.getMyOrders().body()?.data?.let { _orders.value = it }
                }
                val partJob = launch {
                    RetrofitClient.instance.getMyParticipations().body()?.data?.let { _participations.value = it }
                }
                profileJob.join(); ordersJob.join(); partJob.join()
            } catch (e: Exception) { /* ignore */ } finally { _isLoading.value = false }
        }
    }
}

class ProfileEditViewModel : ViewModel() {
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
                val res = RetrofitClient.instance.getMyProfile()
                if (res.isSuccessful) _profile.value = res.body()?.data
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
                val res = RetrofitClient.instance.updateMyProfile(req)
                if (res.isSuccessful) {
                    _toastMessage.value = "프로필이 성공적으로 수정되었습니다."
                    onSuccess()
                }
            } catch (e: Exception) {
                _toastMessage.value = "저장 실패: ${e.message}"
            } finally { _isSaving.value = false }
        }
    }

    fun clearToast() { _toastMessage.value = null }
}
