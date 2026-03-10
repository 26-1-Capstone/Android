package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.ProductDetailViewModel
import java.text.NumberFormat
import java.util.Locale

// frontend: ProductDetailPage.jsx
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val product by viewModel.product.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    if (isLoading) {
        AppScaffold(navController = navController, context = context, showBack = true) { LoadingScreen() }
        return
    }

    val p = product ?: run {
        AppScaffold(navController = navController, context = context, showBack = true) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("존재하지 않는 상품입니다.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigateUp() }) { Text("뒤로 가기") }
                }
            }
        }
        return
    }

    val isSoldOut = p.stockQuantity == 0

    Scaffold(
        topBar = {
            AppHeader(navController = navController, showBack = true, showSearch = false, showCart = true)
        },
        bottomBar = {
            // Fixed Bottom Action Bar (frontend의 .bottom-action-bar와 동일)
            Surface(shadowElevation = 8.dp, tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.addToCart {} },
                        modifier = Modifier.weight(1f).height(52.dp),
                        enabled = !isSoldOut
                    ) { Text("장바구니") }
                    Button(
                        onClick = {
                            // 1. Checkout 경로에 파라미터 추가
                            // NavGraph에 정의된 형태: "checkout?productId={productId}&quantity={quantity}"
                            val route = "checkout?productId=${p.id}&quantity=$quantity"

                            // 2. 이동하기 전, 혹시 남아있을지 모를 장바구니 보따리를 비워줍니다 (선택사항이지만 안전함)
                            navController.currentBackStackEntry?.savedStateHandle?.remove<List<Any>>("checkoutItems")

                            // 3. 이동
                            navController.navigate(route)
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        enabled = !isSoldOut
                    ) {
                        Text(if (isSoldOut) "품절" else "바로 구매")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 상품 이미지
            AsyncImage(
                model = p.imageUrl ?: "https://via.placeholder.com/400x400",
                contentDescription = p.name,
                modifier = Modifier.fillMaxWidth().height(280.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(20.dp)) {
                p.categoryName?.let { Text(text = it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
                Spacer(Modifier.height(8.dp))
                Text(text = p.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(p.price)}원",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text("NutriShare 공동구매를 통해 더욱 저렴하게 만나보세요.\n신선하고 안전한 배송을 약속합니다.", style = MaterialTheme.typography.bodyMedium)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                // 수량 선택
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("수량", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    if (isSoldOut) {
                        Text("품절", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    } else {
                        QuantitySelector(value = quantity, max = p.stockQuantity, onValueChange = { viewModel.setQuantity(it) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("총 상품 금액", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(if (isSoldOut) 0 else p.price * quantity)}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // Toast
    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearToast()
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
        }
    }
}
