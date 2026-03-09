package com.example.nutrishare_android.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.nutrishare_android.data.local.AuthStorage

// frontend: PageLayout.jsx — Header + BottomNav + content 래퍼
@Composable
fun AppScaffold(
    navController: NavController,
    context: Context,
    titleHeader: String? = null,
    showBack: Boolean = false,
    showSearch: Boolean = true,
    showCart: Boolean = true,
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val authStorage = AuthStorage(context)

    Scaffold(
        topBar = {
            AppHeader(
                navController = navController,
                title = titleHeader,
                showBack = showBack,
                showSearch = showSearch,
                showCart = showCart
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController, authStorage = authStorage)
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
