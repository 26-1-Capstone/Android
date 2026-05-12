package com.example.nutrishare_android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.nutrishare_android.ui.components.AddressData
import com.example.nutrishare_android.ui.components.AddressForm
import com.example.nutrishare_android.ui.components.AppScaffold
import com.example.nutrishare_android.ui.components.LoadingScreen
import com.example.nutrishare_android.ui.viewmodel.ProfileEditViewModel

@Composable
fun ProfileEditScreen(
    navController: NavController,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
                zipcode = p.address?.zipCode.orEmpty(),
                basicAddress = p.address?.addressLine1.orEmpty(),
                detailAddress = p.address?.addressLine2.orEmpty()
            )
        }
    }

    AppScaffold(
        navController = navController,
        context = context,
        titleHeader = "프로필 수정",
        showBack = true,
        showSearch = false,
        showCart = false,
        showBottomBar = false
    ) { innerPadding ->
        if (isLoading) {
            LoadingScreen()
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("기본 정보", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text("닉네임", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("닉네임을 입력하세요") }
                        )
                    }
                    Column {
                        Text("이메일 (변경 불가)", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = profile?.email.orEmpty(),
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true
                        )
                    }
                    Button(
                        onClick = {
                            val addr = savedAddress ?: AddressData()
                            viewModel.save(nickname, addr) { navController.navigateUp() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "저장 중..." else "기본 정보 저장")
                    }
                }
            }

            Divider()

            Text("기본 배송지 관리", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "공동구매 결제 완료 후 상품을 편하게 받아볼 배송지를 등록해주세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
            AddressForm(
                initialData = savedAddress ?: AddressData(),
                onSubmit = { addr ->
                    savedAddress = addr
                    viewModel.save(nickname, addr) {
                        savedAddress = addr
                    }
                },
                onAddressChange = { addr ->
                    savedAddress = addr
                },
                submitLabel = "배송지 저장",
                isSubmitting = isSaving,
                addressActionLabel = "저장된 배송지 불러오기",
                onAddressAction = { _, updateAddress ->
                    val address = profile?.address
                    if (address == null || address.zipCode.isNullOrBlank() || address.addressLine1.isNullOrBlank()) {
                        viewModel.showToast("저장된 배송지가 없습니다.")
                    } else {
                        val loadedAddress = AddressData(
                            zipcode = address.zipCode,
                            basicAddress = address.addressLine1,
                            detailAddress = address.addressLine2.orEmpty()
                        )
                        updateAddress(loadedAddress)
                        savedAddress = loadedAddress
                        viewModel.showToast("저장된 배송지를 불러왔습니다.")
                    }
                }
            )
        }
    }

    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
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
