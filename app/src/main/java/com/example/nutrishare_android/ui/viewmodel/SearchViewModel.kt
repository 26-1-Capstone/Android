package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.repository.NutriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NutriRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Product>>(emptyList())
    val results: StateFlow<List<Product>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // frontend: query가 변경될 때 자동 검색
    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .filter { it.isNotBlank() }
                .collectLatest { q -> doSearch(q) }
        }
    }

    fun setQuery(q: String) {
        _query.value = q
        if (q.isBlank()) _results.value = emptyList()
    }

    fun search() {
        val q = _query.value.trim()
        if (q.isNotBlank()) {
            viewModelScope.launch { doSearch(q) }
        }
    }

    private suspend fun doSearch(q: String) {
        _isLoading.value = true
        try {
            repository.searchProducts(q, 50)
                .onSuccess { _results.value = it.content }
                .onFailure { _results.value = emptyList() }
        } catch (e: Exception) {
            _results.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
}
