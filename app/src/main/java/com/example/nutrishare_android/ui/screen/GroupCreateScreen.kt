package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.AppScaffold
import com.example.nutrishare_android.ui.viewmodel.GroupCreateViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCreateScreen(
    navController: NavController,
    viewModel: GroupCreateViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var title by remember { mutableStateOf("") }
    var targetQuantity by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AppScaffold(
        navController = navController,
        context = context,
        titleHeader = "공동구매 열기",
        showBack = true,
        showSearch = false,
        showCart = false,
        showBottomBar = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "이웃과 나누고 싶은 상품을 선택해 모집글을 작성해보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
            )

            Column {
                Text("상품 선택 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = viewModel.productOptions.find { it.first == selectedProductId }?.second
                            ?: "공동구매할 상품을 선택하세요",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        viewModel.productOptions.forEach { (id, name, price) ->
                            DropdownMenuItem(
                                text = { Text("$name (정상가: ${"%,d".format(price)}원)") },
                                onClick = {
                                    selectedProductId = id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Text(
                    "현재 시스템에 등록된 상품만 공동구매를 주최할 수 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }

            Column {
                Text("모집 제목 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 50) title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("예) 마트보다 싼 라면 1박스 같이 사실 분!") },
                    singleLine = true,
                    supportingText = { Text("${title.length}/50") }
                )
            }

            BoxWithConstraints {
                @Composable
                fun TargetField(modifier: Modifier) {
                    Column(modifier) {
                        Text("목표 인원 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = targetQuantity,
                            onValueChange = { targetQuantity = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("최소 10명") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                @Composable
                fun DueDateField(modifier: Modifier) {
                    Column(modifier) {
                        Text("마감일 (필수)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("yyyy-MM-dd") },
                            singleLine = true
                        )
                    }
                }

                if (maxWidth < 420.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TargetField(Modifier.fillMaxWidth())
                        DueDateField(Modifier.fillMaxWidth())
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TargetField(Modifier.weight(1f))
                        DueDateField(Modifier.weight(1f))
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("공동구매 주최 가이드", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("목표 인원이 달성되면 할인 가격으로 결제 및 배송이 확정됩니다.", style = MaterialTheme.typography.bodySmall)
                    Text("마감일 이전에 인원이 모두 모이면 즉시 결제가 진행될 수 있습니다.", style = MaterialTheme.typography.bodySmall)
                    Text("부적절한 내용이나 목적과 맞지 않는 글은 무통보 삭제될 수 있습니다.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = {
                    val productId = selectedProductId ?: run {
                        viewModel.showToast("상품을 선택해주세요.")
                        return@Button
                    }
                    val qty = targetQuantity.toIntOrNull() ?: run {
                        viewModel.showToast("목표 인원은 숫자로 입력해주세요.")
                        return@Button
                    }
                    if (qty <= 0) {
                        viewModel.showToast("목표 인원은 1명 이상이어야 합니다.")
                        return@Button
                    }
                    if (!Regex("""\d{4}-\d{2}-\d{2}""").matches(dueDate)) {
                        viewModel.showToast("마감일은 yyyy-MM-dd 형식으로 입력해주세요.")
                        return@Button
                    }

                    viewModel.createGroup(productId, title, qty, dueDate) {
                        navController.navigate(Screen.GroupList.route) {
                            popUpTo(Screen.GroupList.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSubmitting &&
                    selectedProductId != null &&
                    title.isNotBlank() &&
                    targetQuantity.isNotBlank() &&
                    dueDate.isNotBlank()
            ) {
                Text(if (isSubmitting) "진행 중..." else "모집 시작하기", fontWeight = FontWeight.Bold)
            }
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            delay(3000)
            viewModel.clearToast()
        }
        Box(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            Snackbar(Modifier.padding(16.dp)) { Text(msg) }
        }
    }
}
