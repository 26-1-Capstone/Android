package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// frontend: AddressForm.jsx
data class AddressData(
    val zipcode: String = "",
    val basicAddress: String = "",
    val detailAddress: String = ""
)

@Composable
fun AddressForm(
    initialData: AddressData = AddressData(),
    onSubmit: (AddressData) -> Unit,
    onAddressChange: ((AddressData) -> Unit)? = null,
    submitLabel: String = "배송지 저장",
    isSubmitting: Boolean = false,
    addressActionLabel: String = "주소 찾기",
    onAddressAction: ((currentAddress: AddressData, updateAddress: (AddressData) -> Unit) -> Unit)? = null
) {
    var address by remember(initialData) {
        mutableStateOf(initialData)
    }

    fun updateAddress(newAddress: AddressData) {
        address = newAddress
        onAddressChange?.invoke(newAddress)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 우편번호
        Column {
            Text("우편번호", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = address.zipcode,
                    onValueChange = { updateAddress(address.copy(zipcode = it)) },
                    placeholder = { Text("00000") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                FilledTonalButton(
                    onClick = {
                        if (onAddressAction != null) {
                            onAddressAction(address) { newAddress -> updateAddress(newAddress) }
                        } else {
                            // 임시: frontend와 동일하게 더미 주소 삽입 (Daum Postcode 연동 필요)
                            updateAddress(
                                address.copy(
                                    zipcode = "06236",
                                    basicAddress = "서울 강남구 테헤란로 152"
                                )
                            )
                        }
                    }
                ) {
                    Text(addressActionLabel)
                }
            }
        }
        // 기본 주소
        Column {
            Text("기본 주소", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = address.basicAddress,
                onValueChange = { updateAddress(address.copy(basicAddress = it)) },
                placeholder = { Text("주소를 검색해주세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        // 상세 주소
        Column {
            Text("상세 주소", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = address.detailAddress,
                onValueChange = { updateAddress(address.copy(detailAddress = it)) },
                placeholder = { Text("나머지 상세 주소를 입력해주세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Button(
            onClick = { onSubmit(address) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(if (isSubmitting) "저장 중..." else submitLabel)
        }
    }
}
