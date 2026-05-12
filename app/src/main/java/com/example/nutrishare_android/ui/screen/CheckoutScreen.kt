package com.example.nutrishare_android.ui.screen

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.BuildConfig
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.AddressData
import com.example.nutrishare_android.ui.components.AddressForm
import com.example.nutrishare_android.ui.components.AppScaffold
import com.example.nutrishare_android.ui.components.LoadingScreen
import com.example.nutrishare_android.ui.viewmodel.CheckoutItem
import com.example.nutrishare_android.ui.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutScreen(
    navController: NavController,
    productId: Long?,
    quantity: Int,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val savedItems = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<List<CheckoutItem>>("checkoutItems")

        if (!savedItems.isNullOrEmpty()) {
            debugLog("Screen -> ViewModel items: ${savedItems.size}")
            viewModel.setCheckoutItems(savedItems)
        } else {
            viewModel.initData(productId, quantity)
        }
    }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val checkoutItems by viewModel.checkoutItems.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    var savedAddress by remember { mutableStateOf<AddressData?>(null) }

    val totalAmount = checkoutItems.sumOf { it.unitPrice * it.quantity }
    LaunchedEffect(checkoutItems, isLoading) {
        debugLog("State changed - isLoading: $isLoading, itemsSize: ${checkoutItems.size}")
        if (checkoutItems.isNotEmpty()) {
            debugLog("First item: ${checkoutItems[0].productName}")
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else if (checkoutItems.isEmpty()) {
        debugLog("Checkout items missing; showing empty state")
        AppScaffold(
            navController = navController,
            context = context,
            titleHeader = "주문하기",
            showBack = true,
            showBottomBar = false
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("주문할 상품이 없습니다.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { navController.navigateUp() }) {
                        Text("돌아가기")
                    }
                }
            }
        }
        return
    }

    AppScaffold(
        navController = navController,
        context = context,
        titleHeader = "주문하기",
        showBack = true,
        showCart = false,
        showSearch = false,
        showBottomBar = false
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("주문 상품", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        checkoutItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.productName} x ${item.quantity}", modifier = Modifier.weight(1f))
                                Text(
                                    "${NumberFormat.getNumberInstance(Locale.KOREA).format(item.unitPrice * item.quantity)}원",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("배송지", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AddressForm(
                    initialData = savedAddress ?: AddressData(),
                    onSubmit = { address ->
                        savedAddress = address
                        viewModel.showToast("배송지가 확인되었습니다.")
                    },
                    onAddressChange = { address ->
                        savedAddress = address
                    },
                    submitLabel = "배송지 확인",
                    addressActionLabel = "저장된 배송지 불러오기",
                    onAddressAction = { _, updateAddress ->
                        viewModel.loadSavedAddress { address ->
                            updateAddress(address)
                            savedAddress = address
                        }
                    }
                )
            }

            item {
                Text("결제 요약", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("상품 금액")
                            Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("배송비")
                            Text(
                                "무료",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Divider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("최종 결제 금액", fontWeight = FontWeight.Bold)
                            Text(
                                "${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val address = savedAddress ?: run {
                            viewModel.showToast("배송지를 먼저 확인해 주세요.")
                            return@Button
                        }
                        viewModel.submitOrder(checkoutItems, address) { orderId ->
                            navController.navigate(Screen.OrderComplete.createRoute(orderId)) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !isSubmitting
                ) {
                    Text(
                        if (isSubmitting) {
                            "처리 중..."
                        } else {
                            "${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)}원 결제하기"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    toastMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearToast()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            Snackbar(Modifier.padding(16.dp)) { Text(message) }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
            Snackbar(Modifier.padding(16.dp)) { Text(message) }
        }
    }
}

private fun debugLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("CheckoutLog", message)
    }
}

private fun debugLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("CheckoutLog", message)
    }
}
