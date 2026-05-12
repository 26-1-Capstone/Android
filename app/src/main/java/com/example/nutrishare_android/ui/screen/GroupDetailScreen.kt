package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.navigation.navigateToTopLevel
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.GroupDetailViewModel
import java.text.NumberFormat
import java.util.Locale

// frontend: GroupDetailPage.jsx
@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: Long,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val group by viewModel.group.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isParticipating by viewModel.isParticipating.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(groupId) { viewModel.loadGroup(groupId) }

    if (isLoading) { AppScaffold(navController, context, showBack = true) { LoadingScreen() }; return }

    val g = group ?: run {
        AppScaffold(navController, context, showBack = true) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("존재하지 않는 공동구매입니다.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigate(Screen.GroupList.route) }) { Text("목록으로") }
                }
            }
        }
        return
    }

    val isFull = g.currentQuantity >= g.targetQuantity
    val discountRate = 35

    Scaffold(
        topBar = { AppHeader(navController, showBack = true, showSearch = false) },
        bottomBar = {
            Surface(
                modifier = Modifier.navigationBarsPadding(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { viewModel.participate(groupId) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    enabled = !isFull && !isParticipating,
                    colors = if (isParticipating) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ButtonDefaults.buttonColors()
                ) {
                    Text(
                        when {
                            isParticipating -> "참여 완료"
                            isFull -> "모집 마감"
                            else -> "공동구매 참여하기"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 헤더
            Column {
                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp)) {
                    Text("진행중", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.height(8.dp))
                Text(g.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }

            // 상품 정보 박스
            Card {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("상품", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Column {
                        Text(g.productName, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("$discountRate%", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(g.typePrice)}원", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("정상가: ${NumberFormat.getNumberInstance(Locale.KOREA).format((g.typePrice / (1 - discountRate / 100.0)).toLong())}원", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 모집 현황
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("모집 현황", fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("현재 인원", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text("${g.currentQuantity}명", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("목표 인원", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text("${g.targetQuantity}명", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("남은 시간", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text("3일 4시간", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    NutriProgressBar(g.currentQuantity, g.targetQuantity)
                    Text("목표 인원이 모두 모이면 할인된 가격으로 결제 및 배송이 진행됩니다.", style = MaterialTheme.typography.bodySmall)
                }
            }

            // 주최자 안내
            Card {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("안내", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Column {
                        Text("주최자 안내", fontWeight = FontWeight.SemiBold)
                        Text("\"함께 사면 더 저렴하게 나눌 수 있어요.\"", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(3000); viewModel.clearToast() }
        Box(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(Modifier.padding(16.dp)) { Text(msg) }
        }
    }
}
