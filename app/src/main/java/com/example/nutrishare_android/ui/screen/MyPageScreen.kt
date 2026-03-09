package com.example.nutrishare_android.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.MyPageViewModel
import java.text.NumberFormat
import java.util.Locale

// frontend: MyPage.jsx
@Composable
fun MyPageScreen(
    navController: NavController,
    context: Context,
    viewModel: MyPageViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val participations by viewModel.participations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf("orders") }
    val authStorage = remember { AuthStorage(context) }

    AppScaffold(navController = navController, context = context, titleHeader = "마이페이지", showSearch = false, showCart = false) { innerPadding ->
        if (isLoading) { LoadingScreen(); return@AppScaffold }
        val p = profile ?: return@AppScaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 프로필 헤더
            item {
                Card {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AsyncImage(
                                model = p.profileImageUrl ?: "https://via.placeholder.com/80",
                                contentDescription = "프로필",
                                modifier = Modifier.size(72.dp).clip(CircleShape)
                            )
                            Column(Modifier.weight(1f)) {
                                Text("${p.nickname}님", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(p.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            OutlinedButton(onClick = { navController.navigate(Screen.ProfileEdit.route) }) { Text("수정") }
                        }
                        Spacer(Modifier.height(16.dp))
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("이번 달 공동구매로 절약한 금액", style = MaterialTheme.typography.bodySmall)
                                Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(p.totalSavings ?: 0)}원", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                Text("혼자 샀을 때보다 훨씬 이득이에요!", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 탭 전환
            item {
                TabRow(selectedTabIndex = if (activeTab == "orders") 0 else 1) {
                    Tab(selected = activeTab == "orders", onClick = { activeTab = "orders" }) {
                        Text("주문 내역", modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Tab(selected = activeTab == "groups", onClick = { activeTab = "groups" }) {
                        Text("참여한 공동구매", modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            // 탭 콘텐츠
            if (activeTab == "orders") {
                if (orders.isEmpty()) {
                    item { Text("주문 내역이 없습니다.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp)) }
                } else {
                    items(orders) { order ->
                        Card {
                            Column(Modifier.padding(16.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    val displayDate = order.orderDate?.substringBefore("T") ?: "날짜 없음"
                                    Text(
                                        text = displayDate,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    StatusBadge(order.status)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(order.summary ?: "주문 정보 없음", fontWeight = FontWeight.SemiBold)
                                Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(order.totalAmount)}원", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text("주문번호: ${order.orderId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                    OutlinedButton(onClick = {}, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) { Text("상세 보기", style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                    }
                }
            } else {
                if (participations.isEmpty()) {
                    item { Text("참여 내역이 없습니다.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp)) }
                } else {
                    items(participations) { group ->
                        Card(onClick = { navController.navigate(Screen.GroupDetail.createRoute(group.groupPurchaseId)) }) {
                            Column(Modifier.padding(16.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text(group.title?: "", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                    StatusBadge(group.status?: "")
                                }
                                Text(group.productName?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                Text("모집 상태: ${group.currentQuantity} / ${group.targetQuantity}명", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 로그아웃
            item {
                TextButton(
                    onClick = { authStorage.removeToken(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("로그아웃", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
