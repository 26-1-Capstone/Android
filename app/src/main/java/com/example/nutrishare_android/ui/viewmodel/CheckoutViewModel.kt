package com.example.nutrishare_android.ui.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.CreateOrderRequest
import com.example.nutrishare_android.data.model.OrderItem
import com.example.nutrishare_android.data.model.ShippingAddress
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NutriRepository
) : ViewModel() {

    private val _checkoutItems = MutableStateFlow<List<CheckoutItem>>(emptyList())
    val checkoutItems: StateFlow<List<CheckoutItem>> = _checkoutItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    init {
        // Restore checkout items from SavedStateHandle when available
        viewModelScope.launch {
            savedStateHandle.getStateFlow<List<CheckoutItem>>("checkoutItems", emptyList())
                .collect { items ->
                    if (items.isNotEmpty()) {
                        Log.d("CheckoutLog", "Loaded checkout items: ${items.size}")
                        _checkoutItems.value = items
                        _isLoading.value = false
                    }
                }
        }
    }

    fun setCheckoutItems(items: List<CheckoutItem>) {
        _checkoutItems.value = items
        _isLoading.value = false
        Log.d("CheckoutLog", "Checkout items set: ${items.size}")
    }

    fun initData(productId: Long?, quantity: Int) {
        if (_checkoutItems.value.isNotEmpty()) {
            _isLoading.value = false
            return
        }

        if (productId != null) {
            loadSingleProduct(productId, quantity)
        } else {
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500) // wait up to 1.5s
                if (_checkoutItems.value.isEmpty()) {
                    Log.d("CheckoutLog", "Checkout items missing; stop loading")
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadSingleProduct(productId: Long, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProductDetail(productId)
                    .onSuccess { p ->
                        _checkoutItems.value = listOf(
                            CheckoutItem(
                                productId = p.id,
                                productName = p.name,
                                unitPrice = p.price.toLong(),
                                quantity = quantity
                            )
                        )
                    }
                    .onFailure { _toastMessage.value = "상품 정보를 불러오지 못했습니다." }
            } catch (e: Exception) {
                _toastMessage.value = "오류 발생: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitOrder(
        checkoutItems: List<CheckoutItem>,
        address: com.example.nutrishare_android.ui.components.AddressData,
        onSuccess: (Long) -> Unit
    ) {
        if (address.zipcode.isBlank()) {
            _toastMessage.value = "배송지를 먼저 입력해주세요."
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val payload = CreateOrderRequest(
                    shippingAddress = ShippingAddress(
                        zipCode = address.zipcode,
                        line1 = address.basicAddress,
                        line2 = address.detailAddress
                    ),
                    items = checkoutItems.map { item ->
                        OrderItem(
                            productId = item.productId,
                            productName = item.productName,
                            unitPrice = item.unitPrice,
                            quantity = item.quantity
                        )
                    }
                )
                repository.createOrder(payload)
                    .onSuccess { onSuccess(it.resourceId) }
                    .onFailure { _toastMessage.value = "결제에 실패했습니다." }
            } catch (e: Exception) {
                _toastMessage.value = "결제 오류: ${e.message}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun showToast(msg: String) { _toastMessage.value = msg }
    fun clearToast() { _toastMessage.value = null }
}

@Parcelize
data class CheckoutItem(
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Int
) : Parcelable
