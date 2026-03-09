package com.example.nutrishare_android.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.CartViewModel
import com.example.nutrishare_android.ui.viewmodel.CheckoutItem
import java.text.NumberFormat
import java.util.Locale

// frontend: CartPage.jsx
@Composable
fun CartScreen(
    navController: NavController,
    context: Context = navController.context,
    viewModel: CartViewModel = viewModel()
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalAmount.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    AppScaffold(navController = navController, context = context, titleHeader = "장바구니", showBack = false) { innerPadding ->
        if (isLoading) { LoadingScreen(); return@AppScaffold }

        if (cartItems.isEmpty()) {
            EmptyState(title = "장바구니가 비어있습니다", actionLabel = "쇼핑하러 가기") {
                navController.navigate(Screen.Home.route)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.productId }) { item ->
                        Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AsyncImage(
                                    model = "https://via.placeholder.com/100",
                                    contentDescription = item.productName,
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(item.productName, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                        IconButton(onClick = { showDeleteDialog = item.productId }, modifier = Modifier.size(24.dp)) {
                                            Text("✕", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(item.typePrice)}원", style = MaterialTheme.typography.bodySmall)
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        QuantitySelector(value = item.quantity, max = 99, onValueChange = { viewModel.updateQuantity(item.productId, it) })
                                        Text(
                                            "${NumberFormat.getNumberInstance(Locale.KOREA).format(item.totalPrice)}원",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 결제 요약
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("상품 금액"); Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원")
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("배송비"); Text("무료", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("결제 예상 금액", fontWeight = FontWeight.Bold)
                            Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val itemsToOrder = cartItems.map { item ->
                                    CheckoutItem(
                                        productId = item.productId,
                                        productName = item.productName,
                                        unitPrice = item.typePrice,
                                        quantity = item.quantity
                                    )
                                }

                                // 1. 먼저 페이지를 이동시킵니다.
                                navController.navigate(Screen.Checkout.route)

                                // 2. 이동 직후 '지금 내 눈앞에 있는 화면(Checkout)'의 주머니를 바로 엽니다.
                                // 이것이 가장 확실한 '직배송' 방법입니다.
                                navController.currentBackStackEntry?.savedStateHandle?.set("checkoutItems", itemsToOrder)

                                android.util.Log.d("CheckoutLog", "Checkout(현재 화면) 주머니에 데이터 직배송 완료: ${itemsToOrder.size}개")
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("주문하기", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 삭제 확인 다이얼로그 (frontend: window.confirm과 동일)
    showDeleteDialog?.let { productId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("삭제 확인") },
            text = { Text("장바구니에서 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { viewModel.removeItem(productId); showDeleteDialog = null }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("취소") }
            }
        )
    }
}
