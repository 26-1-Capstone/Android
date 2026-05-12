package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.AppScaffold
import com.example.nutrishare_android.ui.components.EmptyState
import com.example.nutrishare_android.ui.components.LoadingScreen
import com.example.nutrishare_android.ui.components.StatusBadge
import com.example.nutrishare_android.ui.viewmodel.MyPageViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyPageScreen(
    navController: NavController,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val participations by viewModel.participations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isDeletingAccount by viewModel.isDeletingAccount.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf("orders") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.refreshOnStart()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffold(
        navController = navController,
        context = context,
        titleHeader = "마이페이지",
        showSearch = false,
        showCart = false
    ) { innerPadding ->
        if (isLoading) {
            LoadingScreen()
            return@AppScaffold
        }

        val user = profile ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "프로필 정보를 불러오지 못했습니다.",
                    description = "세션이 만료되었거나 네트워크 요청이 실패했습니다.",
                    actionLabel = "로그인으로 이동",
                    onAction = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            return@AppScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                model = user.profileImageUrl ?: "https://via.placeholder.com/80",
                                contentDescription = "프로필 이미지",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = user.nickname,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            OutlinedButton(onClick = { navController.navigate(Screen.ProfileEdit.route) }) {
                                Text("수정")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("절약 금액", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(user.totalSavings ?: 0)}원",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("현재 서버 응답 기준으로 계산됩니다.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            item {
                TabRow(
                    selectedTabIndex = if (activeTab == "orders") 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    divider = {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                    },
                    indicator = { positions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(positions[if (activeTab == "orders") 0 else 1]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(selected = activeTab == "orders", onClick = { activeTab = "orders" }) {
                        Text("주문 내역", modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Tab(selected = activeTab == "groups", onClick = { activeTab = "groups" }) {
                        Text("참여한 공동구매", modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            if (activeTab == "orders") {
                if (orders.isEmpty()) {
                    item {
                        Text(
                            text = "아직 주문 내역이 없습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(orders) { order ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderDate?.substringBefore("T").takeUnless { it.isNullOrBlank() }
                                            ?: "주문일 정보 없음",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    StatusBadge(order.status ?: "")
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(order.summary ?: "주문 상품 정보 없음", fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(order.totalAmount ?: 0)}원",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "주문 번호: ${order.orderId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                if (participations.isEmpty()) {
                    item {
                        Text(
                            text = "아직 참여한 공동구매가 없습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(participations) { group ->
                        Card(
                            onClick = { navController.navigate(Screen.GroupDetail.createRoute(group.groupPurchaseId)) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = group.title ?: "",
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(group.status ?: "")
                                }
                                Text(
                                    text = group.productName ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "진행 수량: ${group.currentQuantity} / ${group.targetQuantity}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeletingAccount,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(if (isDeletingAccount) "탈퇴 처리 중..." else "회원 탈퇴")
                    }
                    TextButton(
                        onClick = {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("로그아웃", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeletingAccount) {
                    showDeleteDialog = false
                }
            },
            title = { Text("회원 탈퇴") },
            text = { Text("정말 회원 탈퇴를 진행하시겠습니까? 탈퇴 후에는 계정 복구가 어려울 수 있습니다.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount {
                            showDeleteDialog = false
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    enabled = !isDeletingAccount,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (isDeletingAccount) "처리 중..." else "탈퇴")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeletingAccount
                ) {
                    Text("취소")
                }
            }
        )
    }

    toastMessage?.let { message ->
        LaunchedEffect(message) {
            delay(3000)
            viewModel.clearToast()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(Modifier.padding(16.dp)) {
                Text(message)
            }
        }
    }
}
