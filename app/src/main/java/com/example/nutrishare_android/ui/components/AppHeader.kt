package com.example.nutrishare_android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.nutrishare_android.navigation.navigateToTopLevel
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
    Surface(color = Color.White) {
        Column {
            TopAppBar(
                title = {
                    if (title != null) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else if (!showBack) {
                        TextButton(onClick = { navController.navigateToTopLevel(Screen.Home.route) }) {
                            Text(
                                text = "NutriShare",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "뒤로",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
                    if (showSearch) {
                        IconButton(onClick = { navController.navigateToTopLevel(Screen.Search.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "검색",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (showCart) {
                        IconButton(onClick = { navController.navigateToTopLevel(Screen.Cart.route) }) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "장바구니",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            )
        }
    }
}
