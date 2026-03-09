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
import com.example.nutrishare_android.ui.components.*
import com.example.nutrishare_android.ui.viewmodel.ProfileEditViewModel

// frontend: ProfileEditPage.jsx
@Composable
fun ProfileEditScreen(
    navController: NavController,
    context: Context = navController.context,
    viewModel: ProfileEditViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    var nickname by remember { mutableStateOf("") }
    var savedAddress by remember { mutableStateOf<AddressData?>(null) }

    LaunchedEffect(profile) {
        profile?.let { p ->
            nickname = p.nickname
            savedAddress = AddressData(
                zipcode = p.address?.zipCode ?: "",
                basicAddress = p.address?.addressLine1 ?: "",
                detailAddress = p.address?.addressLine2 ?: ""
            )
        }
    }

    AppScaffold(navController = navController, context = context, titleHeader = "프로필 수정", showBack = true, showSearch = false, showCart = false, showBottomBar = false) { innerPadding ->
        if (isLoading) { LoadingScreen(); return@AppScaffold }

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 기본 정보
            Text("기본 정보", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text("닉네임", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(value = nickname, onValueChange = { nickname = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("닉네임을 입력하세요") })
                    }
                    Column {
                        Text("이메일 (변경 불가)", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(value = profile?.email ?: "", onValueChange = {}, modifier = Modifier.fillMaxWidth(), enabled = false, singleLine = true)
                    }
                    Button(
                        onClick = {
                            val addr = savedAddress ?: AddressData()
                            viewModel.save(nickname, addr) { navController.navigateUp() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) { Text(if (isSaving) "저장 중..." else "기본 정보 저장") }
                }
            }

            Divider()

            // 배송지 관리
            Text("기본 배송지 관리", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("공동구매 결제 시 식료품을 편하게 받아보실 배송지를 등록해 주세요.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            AddressForm(
                initialData = savedAddress ?: AddressData(),
                onSubmit = { addr -> savedAddress = addr },
                submitLabel = "배송지 저장"
            )

            Divider()

            // 회원 탈퇴
            Text("회원 탈퇴", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            Text("탈퇴 시 참여 중인 공동구매 내역이 모두 취소됩니다.", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("탈퇴하기")
            }
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(3000); viewModel.clearToast() }
        androidx.compose.runtime.CompositionLocalProvider {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.BottomCenter) {
                Snackbar(Modifier.padding(16.dp)) { Text(msg) }
            }
        }
    }
}
