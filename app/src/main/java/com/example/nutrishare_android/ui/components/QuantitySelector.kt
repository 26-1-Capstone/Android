package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// frontend: QuantitySelector.jsx
@Composable
fun QuantitySelector(
    value: Int,
    min: Int = 1,
    max: Int = 99,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton(
            onClick = { if (value > min) onValueChange(value - 1) },
            enabled = value > min,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "수량 줄이기"
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = TextAlign.Center
        )
        FilledTonalButton(
            onClick = { if (value < max) onValueChange(value + 1) },
            enabled = value < max,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "수량 늘리기"
            )
        }
    }
}
