package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.AddToCartRequest
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProductDetail(id)
                    .onSuccess { _product.value = it }
                    .onFailure { _product.value = null }
            } catch (e: Exception) { /* ignore */ } finally { _isLoading.value = false }
        }
    }

    fun setQuantity(q: Int) { _quantity.value = q }

    fun addToCart(onSuccess: () -> Unit) {
        val product = _product.value ?: return
        viewModelScope.launch {
            try {
                val request = AddToCartRequest(product.id, _quantity.value)
                repository.addToCart(request)
                    .onSuccess {
                        _toastMessage.value = "장바구니에 ${_quantity.value}개 담았습니다."
                        onSuccess()
                    }
                    .onFailure { _toastMessage.value = "장바구니 담기에 실패했습니다." }
            } catch (e: Exception) { _toastMessage.value = "장바구니 담기에 실패했습니다." }
        }
    }

    fun clearToast() { _toastMessage.value = null }
}
