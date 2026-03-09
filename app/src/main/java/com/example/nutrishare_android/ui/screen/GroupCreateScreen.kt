package com.example.nutrishare_android.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.GroupCreateViewModel
import kotlinx.coroutines.delay

// frontend: GroupCreatePage.jsx
@Composable
fun GroupCreateScreen(
    navController: NavController,
    context: Context = navController.context,
    viewModel: GroupCreateViewModel = viewModel()
) {
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var title by remember { mutableStateOf("") }
    var targetQuantity by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AppScaffold(navController = navController, context = context, titleHeader = "공동구매 열기", showBack = true, showSearch = false, showCart = false, showBottomBar = false) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("이웃과 나누고 싶은 생필품을 선택해 모집글을 작성해 보세요.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))

            // 상품 선택 (frontend: <select> 대체)
            Column {
                Text("상품 선택 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = viewModel.productOptions.find { it.first == selectedProductId }?.second ?: "공동구매할 상품을 선택하세요",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        viewModel.productOptions.forEach { (id, name, price) ->
                            DropdownMenuItem(
                                text = { Text("$name (정상가: ${"%,d".format(price)}원)") },
                                onClick = { selectedProductId = id; expanded = false }
                            )
                        }
                    }
                }
                Text("현재 시스템에 등록된 상품만 공동구매를 주최할 수 있습니다.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
            }

            // 모집 제목
            Column {
                Text("모집 제목 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = title, onValueChange = { if (it.length <= 50) title = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("예) 마트보다 싼 라면 1박스 같이 사실 분!") }, singleLine = true, supportingText = { Text("${title.length}/50") })
            }

            // 목표 인원 + 마감일 (가로 배치)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("목표 인원 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(value = targetQuantity, onValueChange = { targetQuantity = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("최소 10명") }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                }
                Column(Modifier.weight(1f)) {
                    Text("마감일 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("yyyy-MM-dd") }, singleLine = true)
                }
            }

            // 가이드 박스
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("💡 공동구매 주최 가이드", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("• 목표 인원을 달성해야만 할인된 가격으로 결제 및 배송이 확정됩니다.", style = MaterialTheme.typography.bodySmall)
                    Text("• 마감일 이전에 인원이 모두 모이면 즉시 결제가 진행될 수 있습니다.", style = MaterialTheme.typography.bodySmall)
                    Text("• 부적절한 내용이나 목적과 맞지 않는 글은 무통보 삭제될 수 있습니다.", style = MaterialTheme.typography.bodySmall)
                }
            }

            // 제출
            Button(
                onClick = {
                    val productId = selectedProductId ?: return@Button
                    val qty = targetQuantity.toIntOrNull() ?: return@Button
                    viewModel.createGroup(productId, title, qty, dueDate) {
                        navController.navigate(Screen.GroupList.route) {
                            popUpTo(Screen.GroupList.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isSubmitting && selectedProductId != null && title.isNotBlank() && targetQuantity.isNotBlank() && dueDate.isNotBlank()
            ) { Text(if (isSubmitting) "진행 중..." else "모집 시작하기", fontWeight = FontWeight.Bold) }
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) { delay(3000); viewModel.clearToast() }
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
            Snackbar(Modifier.padding(16.dp)) { Text(msg) }
        }
    }
}
