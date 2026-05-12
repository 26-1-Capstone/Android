package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.CartItem
import com.example.nutrishare_android.data.model.UpdateCartRequest
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
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

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private var serverCartSnapshot: List<CartItem> = emptyList()
    private var hasPendingChanges = false
    private var hasLoadedOnce = false
    private var syncJob: Job? = null

    fun refreshCartOnStart() {
        if (syncJob?.isActive == true || hasPendingChanges) return
        fetchCart(force = true)
    }

    fun fetchCart(force: Boolean = false) {
        if (!force && hasPendingChanges) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCart()
                    .onSuccess { data ->
                        val normalizedItems = data.items.normalized()
                        serverCartSnapshot = normalizedItems
                        applyCartItems(normalizedItems)
                        hasPendingChanges = false
                        hasLoadedOnce = true
                    }
                    .onFailure {
                        if (!hasLoadedOnce) {
                            applyCartItems(emptyList())
                        }
                        _toastMessage.value = "장바구니를 불러오지 못했습니다."
                    }
            } catch (e: Exception) {
                _toastMessage.value = "장바구니를 불러오지 못했습니다."
            } finally { _isLoading.value = false }
        }
    }

    fun updateQuantity(productId: Long, newQty: Int) {
        _cartItems.update { items ->
            items.map { item ->
                if (item.productId == productId) item.withQuantity(newQty) else item
            }
        }
        recalculateTotal()
        hasPendingChanges = true
    }

    fun removeItem(productId: Long) {
        _cartItems.update { items -> items.filterNot { it.productId == productId } }
        recalculateTotal()
        hasPendingChanges = true
    }

    fun syncPendingChanges() {
        if (!hasPendingChanges || syncJob?.isActive == true) return

        val currentItems = _cartItems.value
        val originalItems = serverCartSnapshot

        syncJob = viewModelScope.launch {
            try {
                val currentById = currentItems.associateBy { it.productId }
                val originalById = originalItems.associateBy { it.productId }
                var syncSucceeded = true

                for ((productId, originalItem) in originalById) {
                    val currentItem = currentById[productId]
                    if (currentItem == null) {
                        repository.removeCartItem(productId)
                            .onFailure { syncSucceeded = false }
                    } else if (currentItem.quantity != originalItem.quantity) {
                        repository.updateCartItem(productId, UpdateCartRequest(currentItem.quantity))
                            .onFailure { syncSucceeded = false }
                    }
                }

                if (syncSucceeded) {
                    serverCartSnapshot = currentItems.normalized()
                    hasPendingChanges = false
                } else {
                    _toastMessage.value = "장바구니 변경사항을 저장하지 못했습니다."
                }
            } catch (e: Exception) {
                _toastMessage.value = "장바구니 변경사항을 저장하지 못했습니다."
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun applyCartItems(items: List<CartItem>) {
        _cartItems.value = items
        recalculateTotal()
    }

    private fun recalculateTotal() {
        _totalAmount.value = _cartItems.value.sumOf { it.totalPrice }
    }

    private fun List<CartItem>.normalized(): List<CartItem> =
        map { it.withQuantity(it.quantity) }

    private fun CartItem.withQuantity(quantity: Int): CartItem =
        copy(quantity = quantity, totalPrice = typePrice * quantity)
}
