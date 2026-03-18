package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.AppScaffold

// frontend: OrderCompletePage.jsx
@Composable
fun OrderCompleteScreen(
    navController: NavController,
    orderId: Long
) {
    val context = LocalContext.current
    AppScaffold(navController = navController, context = context, showSearch = false, showCart = false, showBottomBar = true) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 체크 아이콘 (frontend SVG 대체)
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("OK", style = MaterialTheme.typography.displayMedium)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("주문이 완료되었습니다!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "주문 번호: $orderId\n빠르고 안전하게 배송해 드릴게요.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { navController.navigate(Screen.MyPage.route) }) {
                    Text("주문 내역 보기")
                }
                Button(onClick = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                }) {
                    Text("쇼핑 계속하기")
                }
            }
        }
    }
}
