package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.navigation.navigateToTopLevel
import com.example.nutrishare_android.navigation.Screen

// frontend: BottomNav.jsx
data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val requireAuth: Boolean
)

val bottomNavItems = listOf(
    NavItem(Screen.Home, "홈", Icons.Filled.Home, false),
    NavItem(Screen.GroupList, "공동구매", Icons.Filled.List, false),
    NavItem(Screen.Cart, "장바구니", Icons.Filled.ShoppingCart, true),
    NavItem(Screen.MyPage, "MY", Icons.Filled.Person, true),
)

@Composable
fun BottomNavBar(navController: NavController, authStorage: AuthStorage) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column {
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        val target = if (item.requireAuth && !authStorage.isAuthenticated()) {
                            Screen.Login.route
                        } else {
                            item.screen.route
                        }

                        if (currentDestination?.route != target) {
                            navController.navigateToTopLevel(target)
                        }
                    },
                    icon = {
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    )
                )
            }
        }
    }
}
