package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.Screen

// frontend: Header.jsx
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    navController: NavController,
    title: String? = null,
    showBack: Boolean = false,
    showSearch: Boolean = true,
    showCart: Boolean = true
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(text = title, fontWeight = FontWeight.SemiBold)
            } else if (!showBack) {
                // 로고
                TextButton(onClick = { navController.navigate(Screen.Home.route) }) {
                    Text(
                        text = "🥦 NutriShare",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Text("←", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
            }
        },
        actions = {
            if (showSearch) {
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Text("🔍", style = MaterialTheme.typography.titleMedium)
                }
            }
            if (showCart) {
                IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                    Text("🛒", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}
