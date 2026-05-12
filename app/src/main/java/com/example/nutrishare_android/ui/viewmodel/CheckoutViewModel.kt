package com.example.nutrishare_android.ui.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.BuildConfig
import com.example.nutrishare_android.data.model.CreateOrderRequest
import com.example.nutrishare_android.data.model.OrderItem
import com.example.nutrishare_android.data.model.ShippingAddress
import com.example.nutrishare_android.data.repository.NutriRepository
import com.example.nutrishare_android.ui.components.AddressData
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

    private var shouldClearCartAfterOrder = false

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<List<CheckoutItem>>("checkoutItems", emptyList())
                .collect { items ->
                    if (items.isNotEmpty()) {
                        debugLog("Loaded checkout items: ${items.size}")
                        shouldClearCartAfterOrder = true
                        _checkoutItems.value = items
                        _isLoading.value = false
                    }
                }
        }
    }

    fun setCheckoutItems(items: List<CheckoutItem>) {
        shouldClearCartAfterOrder = true
        _checkoutItems.value = items
        _isLoading.value = false
        debugLog("Checkout items set: ${items.size}")
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
                kotlinx.coroutines.delay(1500)
                if (_checkoutItems.value.isEmpty()) {
                    debugLog("Checkout items missing; stop loading")
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadSingleProduct(productId: Long, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            shouldClearCartAfterOrder = false
            try {
                repository.getProductDetail(productId)
                    .onSuccess { product ->
                        _checkoutItems.value = listOf(
                            CheckoutItem(
                                productId = product.id,
                                productName = product.name,
                                unitPrice = product.price,
                                quantity = quantity
                            )
                        )
                    }
                    .onFailure {
                        _toastMessage.value = "상품 정보를 불러오지 못했습니다."
                    }
            } catch (e: Exception) {
                _toastMessage.value = "예상치 못한 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitOrder(
        checkoutItems: List<CheckoutItem>,
        address: AddressData,
        onSuccess: (Long) -> Unit
    ) {
        if (address.zipcode.isBlank()) {
            _toastMessage.value = "배송지를 먼저 입력해 주세요."
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
                    .onSuccess { order ->
                        if (shouldClearCartAfterOrder) {
                            clearOrderedCartItems(checkoutItems)
                        }
                        onSuccess(order.resourceId)
                    }
                    .onFailure {
                        _toastMessage.value = "결제에 실패했습니다."
                    }
            } catch (e: Exception) {
                _toastMessage.value = "결제 오류: ${e.message}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun loadSavedAddress(onLoaded: (AddressData) -> Unit) {
        viewModelScope.launch {
            repository.getMyProfile()
                .onSuccess { profile ->
                    val address = profile.address
                    if (address == null || address.zipCode.isNullOrBlank() || address.addressLine1.isNullOrBlank()) {
                        _toastMessage.value = "저장된 배송지가 없습니다."
                        return@onSuccess
                    }

                    onLoaded(
                        AddressData(
                            zipcode = address.zipCode,
                            basicAddress = address.addressLine1,
                            detailAddress = address.addressLine2.orEmpty()
                        )
                    )
                    _toastMessage.value = "저장된 배송지를 불러왔습니다."
                }
                .onFailure {
                    _toastMessage.value = "저장된 배송지를 불러오지 못했습니다."
                }
        }
    }

    private suspend fun clearOrderedCartItems(items: List<CheckoutItem>) {
        items.map { it.productId }
            .distinct()
            .forEach { productId ->
                repository.removeCartItem(productId)
                    .onFailure { debugLog("Failed to remove ordered cart item: $productId") }
            }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun debugLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("CheckoutLog", message)
        }
    }
}

@Parcelize
data class CheckoutItem(
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Int
) : Parcelable
