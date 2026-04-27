package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.viewmodel.LoginViewModel

// frontend: LoginPage.jsx
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // 이미 로그인된 경우 홈으로 이동
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    fun handleOAuthLogin(provider: String) {
        viewModel.loginWithDev(provider)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로고
            Text(
                text = "NutriShare",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "우리 동네 신선 식재료 공동구매",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 혜택 안내
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("신선", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("대형마트 상품을 우리 동네에서 신선하게", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("절약", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("배송과 유통비 절감으로 배송비 0원", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 카카오 로그인 버튼
            Button(
                onClick = { handleOAuthLogin("kakao") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                } else {
                    Text("카카오로 3초만에 시작하기", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 구글 로그인 버튼
            OutlinedButton(
                onClick = { handleOAuthLogin("google") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text("Google 계정으로 로그인", fontWeight = FontWeight.SemiBold)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "로그인하면 NutriShare 이용약관 및 개인정보처리방침에 동의하게 됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
