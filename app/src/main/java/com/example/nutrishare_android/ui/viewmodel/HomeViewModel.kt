package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { fetchProducts() }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getProducts(size = 10)
                if (response.isSuccessful) {
                    _products.value = response.body()?.data?.content ?: emptyList()
                }
            } catch (e: Exception) {
                // 네트워크 오류 무시
            } finally {
                _isLoading.value = false
            }
        }
    }
}
