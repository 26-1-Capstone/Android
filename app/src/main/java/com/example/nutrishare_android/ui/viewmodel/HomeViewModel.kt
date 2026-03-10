package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { fetchProducts() }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProducts(size = 10)
                    .onSuccess { _products.value = it.content }
                    .onFailure { _products.value = emptyList() }
            } catch (e: Exception) {
                // ignore
            } finally {
                _isLoading.value = false
            }
        }
    }
}
