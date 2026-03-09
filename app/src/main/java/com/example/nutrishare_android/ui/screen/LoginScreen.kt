package com.example.nutrishare_android.ui.screen

import android.content.Context
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
import androidx.navigation.NavController
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.data.network.RetrofitClient
import com.example.nutrishare_android.navigation.Screen
import kotlinx.coroutines.launch

// frontend: LoginPage.jsx
@Composable
fun LoginScreen(navController: NavController, context: Context) {
    val scope = rememberCoroutineScope()
    val authStorage = remember { AuthStorage(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 이미 로그인된 경우 홈으로 이동
    LaunchedEffect(Unit) {
        if (authStorage.isAuthenticated()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    fun handleOAuthLogin(provider: String) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 개발 환경: frontend와 동일하게 dev-login 엔드포인트 사용
                val response = RetrofitClient.instance.devLogin()
                if (response.isSuccessful) {
                    val token = response.body()?.data
                    if (token != null) {
                        authStorage.setToken(token)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        errorMessage = "로그인에 실패했습니다."
                    }
                } else {
                    errorMessage = "로그인에 실패했습니다."
                }
            } catch (e: Exception) {
                errorMessage = "로그인 오류: ${e.message}"
            } finally {
                isLoading = false
            }
        }
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
                text = "우리 동네 알뜰 식료품 공동구매",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 혜택 섹션
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("📦", fontSize = 24.sp)
                        Text("대용량 마트 상품을 소분해서 알뜰하게", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("🚚", fontSize = 24.sp)
                        Text("이웃과 함께사면 배송비 0원", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 카카오 로그인 버튼
            Button(
                onClick = { handleOAuthLogin("kakao") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                } else {
                    Text("카카오로 3초 만에 시작하기", fontWeight = FontWeight.SemiBold)
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
                text = "로그인 시 NutriShare의 이용약관 및 개인정보처리방침에 동의하게 됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
