package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.nutrishare_android.R

@Composable
fun ProductImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    shape: Shape,
    productName: String? = contentDescription,
    categoryName: String? = null
) {
    val normalizedImageUrl = imageUrl?.takeIf { it.isNotBlank() }
    val localImageRes = localProductImageRes(productName = productName, categoryName = categoryName)

    if (normalizedImageUrl != null || localImageRes != null) {
        AsyncImage(
            model = normalizedImageUrl ?: localImageRes,
            contentDescription = contentDescription,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Crop,
            error = localImageRes?.let { painterResource(it) },
            placeholder = localImageRes?.let { painterResource(it) }
        )
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "이미지\n없음",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun localProductImageRes(productName: String?, categoryName: String?): Int? {
    val key = listOfNotNull(productName, categoryName).joinToString(" ").lowercase()
    return when {
        key.contains("딸기") || key.contains("strawberr") -> R.drawable.product_strawberry
        key.contains("라면") || key.contains("ramen") -> R.drawable.product_ramen
        key.contains("휴지") || key.contains("tissue") -> R.drawable.product_tissue
        key.contains("사과") || key.contains("apple") -> R.drawable.product_apple
        else -> null
    }
}
