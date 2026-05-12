package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        Column {
            Text("우편번호", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            BoxWithConstraints {
                val isNarrow = maxWidth < 360.dp
                val input: @Composable (Modifier) -> Unit = { modifier ->
                    OutlinedTextField(
                        value = address.zipcode,
                        onValueChange = { updateAddress(address.copy(zipcode = it)) },
                        placeholder = { Text("00000") },
                        modifier = modifier,
                        singleLine = true
                    )
                }
                val action: @Composable () -> Unit = {
                    FilledTonalButton(
                        onClick = {
                            if (onAddressAction != null) {
                                onAddressAction(address) { newAddress -> updateAddress(newAddress) }
                            } else {
                                updateAddress(
                                    address.copy(
                                        zipcode = "06236",
                                        basicAddress = "서울 강남구 테헤란로 152"
                                    )
                                )
                            }
                        },
                        modifier = if (isNarrow) Modifier.fillMaxWidth() else Modifier
                    ) {
                        Text(addressActionLabel)
                    }
                }

                if (isNarrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        input(Modifier.fillMaxWidth())
                        action()
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        input(Modifier.weight(1f))
                        action()
                    }
                }
            }
        }

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
