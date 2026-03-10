package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.CheckoutItem
import com.example.nutrishare_android.ui.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

// frontend: CheckoutPage.jsx
// checkoutItems는 NavController savedStateHandle을 통해 전달 (CartScreen에서 navigate 후 setResult)
@Composable
fun CheckoutScreen(
    navController: NavController,
    productId: Long?,
    quantity: Int,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // 1. 화면이 처음 켜질 때 한 번 실행
    LaunchedEffect(Unit) {
        val savedItems = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<List<CheckoutItem>>("checkoutItems")

        if (!savedItems.isNullOrEmpty()) {
            android.util.Log.d("CheckoutLog", "Screen -> ViewModel items: ${savedItems.size}")
            viewModel.setCheckoutItems(savedItems)
        } else {
            // 상세페이지 구매인 경우 등
            viewModel.initData(productId, quantity)
        }
    }

    // 2. 뷰모델이 준비한 아이템 리스트를 가져옴
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val checkoutItems by viewModel.checkoutItems.collectAsStateWithLifecycle()

    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    var savedAddress by remember { mutableStateOf<AddressData?>(null) }

    val totalAmount = checkoutItems.sumOf { it.unitPrice * it.quantity }
    LaunchedEffect(checkoutItems, isLoading) {
        android.util.Log.d("CheckoutLog", "상태 변경 - isLoading: $isLoading, itemsSize: ${checkoutItems.size}")
        if (checkoutItems.isNotEmpty()) {
            android.util.Log.d("CheckoutLog", "첫 번째 아이템: ${checkoutItems[0].productName}")
        }
    }
    if (isLoading) {
        LoadingScreen()
    } else if (checkoutItems.isEmpty()) {
        android.util.Log.d("CheckoutLog", "데이터가 비어있어서 '주문 불가' 화면 표시")
        AppScaffold(navController = navController, context = context, titleHeader = "주문/결제", showBack = true, showBottomBar = false) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("주문할 상품이 없습니다.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { navController.navigateUp() }) { Text("뒤로 가기") }
                }
            }
        }
        return
    }

    AppScaffold(navController = navController, context = context, titleHeader = "주문 / 결제", showBack = true, showCart = false, showSearch = false, showBottomBar = false) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 주문 상품 정보
            item {
                Text("주문 상품 정보", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        checkoutItems.forEach { item ->
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text("${item.productName} · ${item.quantity}개", modifier = Modifier.weight(1f))
                                Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(item.unitPrice * item.quantity)}원", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // 배송지 입력
            item {
                Text("배송지 입력", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AddressForm(
                    onSubmit = { addr ->
                        savedAddress = addr
                        viewModel.showToast("배송지가 확인되었습니다. 결제 버튼을 눌러주세요.")
                    }
                )
            }

            // 결제 요약
            item {
                Text("결제 요약", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("상품 총액"); Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원") }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("배송비"); Text("무료 (공동구매 특가)", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) }
                        Divider()
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("최종 결제 금액", fontWeight = FontWeight.Bold)
                            Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 결제 버튼
            item {
                Button(
                    onClick = {
                        val addr = savedAddress ?: run { viewModel.showToast("배송지를 먼저 저장해주세요."); return@Button }
                        viewModel.submitOrder(checkoutItems, addr) { orderId ->
                            navController.navigate(Screen.OrderComplete.createRoute(orderId)) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = !isSubmitting
                ) {
                    Text(if (isSubmitting) "결제 진행 중..." else "${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원 결제하기", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(3000); viewModel.clearToast() }
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
            Snackbar(Modifier.padding(16.dp)) { Text(msg) }
        }
    }
}
