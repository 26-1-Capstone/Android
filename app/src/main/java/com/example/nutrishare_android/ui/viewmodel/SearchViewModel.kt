package com.example.nutrishare_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.network.RetrofitClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Product>>(emptyList())
    val results: StateFlow<List<Product>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // frontend: query가 변경될 때 자동 검색 (useEffect + fetchSearchResults와 동일)
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
            val response = RetrofitClient.instance.searchProducts(q, 50)
            if (response.isSuccessful) {
                _results.value = response.body()?.data?.content ?: emptyList()
            }
        } catch (e: Exception) {
            _results.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
}
