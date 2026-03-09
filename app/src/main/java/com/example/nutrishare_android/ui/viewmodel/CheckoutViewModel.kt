package com.example.nutrishare_android.ui.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.*
import com.example.nutrishare_android.data.network.RetrofitClient
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val savedStateHandle: SavedStateHandle
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
        // ⭐ 핵심: 주머니(SavedStateHandle)를 실시간으로 감시합니다.
        viewModelScope.launch {
            savedStateHandle.getStateFlow<List<CheckoutItem>>("checkoutItems", emptyList())
                .collect { items ->
                    if (items.isNotEmpty()) {
                        Log.d("CheckoutLog", "보따리 데이터 수신 완료: ${items.size}개")
                        _checkoutItems.value = items
                        _isLoading.value = false
                    }
                }
        }
    }
    fun setCheckoutItems(items: List<CheckoutItem>) {
        _checkoutItems.value = items
        _isLoading.value = false
        android.util.Log.d("CheckoutLog", "뷰모델 데이터 수신 완료: itemsSize ${items.size}")
    }


    fun initData(productId: Long?, quantity: Int) {
        // 1. 이미 데이터가 있다면(장바구니에서 이미 수신됨) 중복 실행 방지
        if (_checkoutItems.value.isNotEmpty()) {
            _isLoading.value = false
            return
        }

        if (productId != null) {
            // 2. 단일 상품 구매인 경우 서버 조회
            loadSingleProduct(productId, quantity)
        } else {
            // 3. 장바구니 구매인데 아직 데이터가 안 왔다면 조금 더 기다려봅니다.
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500) // 최대 1.5초 대기
                if (_checkoutItems.value.isEmpty()) {
                    Log.d("CheckoutLog", "대기 후에도 데이터가 없어 로딩을 종료합니다.")
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadSingleProduct(productId: Long, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getProductDetail(productId)
                if (response.isSuccessful) {
                    val p = response.body()?.data
                    if (p != null) {
                        _checkoutItems.value = listOf(
                            CheckoutItem(
                                productId = p.id,
                                productName = p.name,
                                unitPrice = p.price.toLong(),
                                quantity = quantity
                            )
                        )
                    }
                } else {
                    _toastMessage.value = "상품 정보를 불러오지 못했습니다."
                }
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
            _toastMessage.value = "배송지를 먼저 저장해주세요."
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
                val response = RetrofitClient.instance.createOrder(payload)
                if (response.isSuccessful) {
                    val orderId = response.body()?.data?.resourceId ?: 0L
                    onSuccess(orderId)
                } else {
                    _toastMessage.value = "결제에 실패했습니다."
                }
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