package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.CreateGroupRequest
import com.example.nutrishare_android.data.model.Group
import com.example.nutrishare_android.data.model.JoinGroupRequest
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _filter = MutableStateFlow("ALL")
    val filter: StateFlow<String> = _filter

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { fetchGroups("ALL") }

    fun fetchGroups(filter: String) {
        _filter.value = filter
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getGroups(50)
                    .onSuccess { page ->
                        var list = page.content
                        if (filter == "CLOSING_SOON") {
                            list = list.filter { group ->
                                group.dueDate?.let { dueStr ->
                                    try {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                        val dueDate = sdf.parse(dueStr) ?: return@let false
                                        val diff = dueDate.time - System.currentTimeMillis()
                                        diff > 0 && diff < 1000L * 60 * 60 * 24 * 3
                                    } catch (e: Exception) { false }
                                } ?: false
                            }
                        }
                        _groups.value = list
                    }
            } catch (e: Exception) { } finally { _isLoading.value = false }
        }
    }
}

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {
    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isParticipating = MutableStateFlow(false)
    val isParticipating: StateFlow<Boolean> = _isParticipating

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun loadGroup(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _isParticipating.value = false
            try {
                repository.getGroupDetail(id).onSuccess { _group.value = it }
                repository.getMyParticipations().onSuccess { participations ->
                    _isParticipating.value = participations.any { it.groupPurchaseId == id }
                }
            } catch (e: Exception) { } finally { _isLoading.value = false }
        }
    }

    fun participate(id: Long) {
        viewModelScope.launch {
            _isParticipating.value = true
            try {
                repository.joinGroup(id, JoinGroupRequest(quantity = 1))
                    .onSuccess {
                        _toastMessage.value = "공동구매에 참여했습니다! 인원이 다 차면 결제가 진행됩니다."
                        repository.getGroupDetail(id).onSuccess { _group.value = it }
                    }
                    .onFailure {
                        _isParticipating.value = false
                        _toastMessage.value = "참여에 실패했습니다."
                    }
            } catch (e: Exception) {
                _isParticipating.value = false
                _toastMessage.value = "오류: ${e.message}"
            }
        }
    }

    fun clearToast() { _toastMessage.value = null }
}

@HiltViewModel
class GroupCreateViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    // frontend GroupCreatePage.jsx의 productOptions와 동일
    val productOptions = listOf(
        Triple(1L, "진라면 매운맛 1박스 (40개)", 28000L),
        Triple(2L, "제주 생수 2L 12병", 13000L),
        Triple(3L, "동원참치 라이트스탠다드 100g 10캔", 22000L)
    )

    fun createGroup(
        productId: Long,
        title: String,
        targetQuantity: Int,
        dueDate: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val unitPrice = productOptions.find { it.first == productId }?.third ?: 10000L
                val endAt = try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val sdfOut = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdfOut.format(sdf.parse(dueDate) ?: java.util.Date())
                } catch (e: Exception) { dueDate }

                val request = CreateGroupRequest(productId, title, targetQuantity, unitPrice, endAt)
                repository.createGroup(request)
                    .onSuccess {
                        _toastMessage.value = "공동구매 모집을 시작했습니다."
                        onSuccess()
                    }
                    .onFailure { _toastMessage.value = "공동구매 생성에 실패했습니다." }
            } catch (e: Exception) {
                _toastMessage.value = "오류: ${e.message}"
            } finally { _isSubmitting.value = false }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() { _toastMessage.value = null }
}
