package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.CartItem
import com.example.nutrishare_android.data.model.UpdateCartRequest
import com.example.nutrishare_android.data.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _totalAmount = MutableStateFlow(0L)
    val totalAmount: StateFlow<Long> = _totalAmount

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { fetchCart() }

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getCart()
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    _cartItems.value = data?.items ?: emptyList()
                    _totalAmount.value = data?.totalAmount ?: 0L
                }
            } catch (e: Exception) { /* ignore */ } finally { _isLoading.value = false }
        }
    }

    fun updateQuantity(productId: Long, newQty: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateCartItem(productId, UpdateCartRequest(newQty))
                fetchCart()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun removeItem(productId: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.removeCartItem(productId)
                fetchCart()
            } catch (e: Exception) { /* ignore */ }
        }
    }
}
