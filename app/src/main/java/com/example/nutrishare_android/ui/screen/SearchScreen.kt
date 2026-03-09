package com.example.nutrishare_android.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.SearchViewModel

// frontend: SearchPage.jsx
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel(),
    context: Context = navController.context
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    AppScaffold(navController = navController, context = context, titleHeader = "검색", showBack = true, showSearch = false) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = { Text("찾으시는 상품을 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search()
                    focusManager.clearFocus()
                }),
                trailingIcon = {
                    TextButton(onClick = {
                        viewModel.search()
                        focusManager.clearFocus()
                    }) { Text("검색") }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> LoadingScreen()
                query.isNotBlank() && results.isNotEmpty() -> {
                    Text(
                        text = "'$query' 검색 결과 ${results.size}건",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(results) { product ->
                            ProductCard(product = product) {
                                navController.navigate(Screen.ProductDetail.createRoute(product.id))
                            }
                        }
                    }
                }
                query.isNotBlank() && results.isEmpty() -> {
                    EmptyState(
                        title = "검색 결과가 없습니다.",
                        description = "다른 검색어 필터를 사용해 보세요."
                    )
                }
                else -> {
                    Text(
                        text = "검색어를 입력해 맛있는 생필품을 찾아보세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
