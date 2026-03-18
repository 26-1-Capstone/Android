package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.GroupListViewModel

// frontend: GroupListPage.jsx
@Composable
fun GroupListScreen(
    navController: NavController,
    viewModel: GroupListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    AppScaffold(navController = navController, context = context) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) { LoadingScreen(); return@Box }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("공동구매 모음", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("이웃들과 함께 생필품을 저렴하게 구매하세요!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    Spacer(Modifier.height(12.dp))
                    // 필터 칩 (frontend .filter-chips와 동일)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf("ALL" to "전체", "CLOSING_SOON" to "마감 임박 💥", "POPULAR" to "인기 높은 🔥")
                        items(filters) { (key, label) ->
                            FilterChip(
                                selected = filter == key,
                                onClick = { viewModel.fetchGroups(key) },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                if (groups.isEmpty()) {
                    item {
                        EmptyState(
                            title = "현재 진행 중인 공동구매가 없습니다.",
                            description = "원하는 상품으로 직접 공동구매를 주최해 보세요!"
                        )
                    }
                } else {
                    items(groups) { group ->
                        GroupBuyingCard(group = group) {
                            navController.navigate(Screen.GroupDetail.createRoute(group.id))
                        }
                    }
                }

                item { Spacer(Modifier.height(72.dp)) } // FAB 공간
            }

            // FAB (frontend의 .fab-btn과 동일)
            FloatingActionButton(
                onClick = { navController.navigate(Screen.GroupCreate.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .padding(end = 16.dp, bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "모집글 작성",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
