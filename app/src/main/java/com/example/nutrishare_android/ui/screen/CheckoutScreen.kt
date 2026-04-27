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
            titleHeader = "Checkout",
            showBack = true,
            showBottomBar = false
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("No items to order.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { navController.navigateUp() }) {
                        Text("Go back")
                    }
                }
            }
        }
        return
    }

    AppScaffold(
        navController = navController,
        context = context,
        titleHeader = "Checkout",
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
                Text("Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                                    "${NumberFormat.getNumberInstance(Locale.KOREA).format(item.unitPrice * item.quantity)} KRW",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("Shipping", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AddressForm(
                    onSubmit = { address ->
                        savedAddress = address
                        viewModel.showToast("Shipping address confirmed.")
                    }
                )
            }

            item {
                Text("Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Items total")
                            Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)} KRW")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping fee")
                            Text(
                                "Free",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Divider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Final total", fontWeight = FontWeight.Bold)
                            Text(
                                "${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)} KRW",
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
                            viewModel.showToast("Please confirm the shipping address first.")
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
                            "Processing..."
                        } else {
                            "Pay ${NumberFormat.getNumberInstance(Locale.KOREA).format(totalAmount)} KRW"
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
