package com.example.nutrishare_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.navigation.Screen
import androidx.navigation.NavDestination.Companion.hierarchy
// frontend: BottomNav.jsx — 홈, 공동구매, 장바구니, MY
data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: String,
    val requireAuth: Boolean
)

val bottomNavItems = listOf(
    NavItem(Screen.Home, "홈", "🏠", false),
    NavItem(Screen.GroupList, "공동구매", "🤝", false),
    NavItem(Screen.Cart, "장바구니", "🛒", true),
    NavItem(Screen.MyPage, "MY", "👤", true),
)

@Composable
fun BottomNavBar(navController: NavController, authStorage: AuthStorage) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            // 현재 아이템이 선택되었는지 확인 (하위 경로 포함 판단)
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    val target = if (item.requireAuth && !authStorage.isAuthenticated()) {
                        Screen.Login.route
                    } else {
                        item.screen.route
                    }

                    // 중복 클릭 방지 (이미 해당 화면이면 이동 안 함)
                    if (currentDestination?.route != target) {
                        navController.navigate(target) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(text = item.icon, style = MaterialTheme.typography.titleLarge)
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
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
