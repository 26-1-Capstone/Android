package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.CartItem
import com.example.nutrishare_android.data.model.UpdateCartRequest
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {
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
                repository.getCart()
                    .onSuccess { data ->
                        _cartItems.value = data.items
                        _totalAmount.value = data.totalAmount
                    }
                    .onFailure {
                        _cartItems.value = emptyList()
                        _totalAmount.value = 0L
                    }
            } catch (e: Exception) {
                // ignore
            } finally { _isLoading.value = false }
        }
    }

    fun updateQuantity(productId: Long, newQty: Int) {
        viewModelScope.launch {
            try {
                repository.updateCartItem(productId, UpdateCartRequest(newQty))
                fetchCart()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun removeItem(productId: Long) {
        viewModelScope.launch {
            try {
                repository.removeCartItem(productId)
                fetchCart()
            } catch (e: Exception) { /* ignore */ }
        }
    }
}
