package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// frontend: ProgressBar.jsx
@Composable
fun NutriProgressBar(value: Int, max: Int) {
    val progress = if (max > 0) (value.toFloat() / max.toFloat()).coerceIn(0f, 1f) else 0f
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer,
        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}
