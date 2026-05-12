package com.example.nutrishare_android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavController.navigateToTopLevel(route: String) {
    val isCurrentDestination = currentDestination?.hierarchy?.any { it.route == route } == true
    if (isCurrentDestination) return

    navigate(route) {
        launchSingleTop = true
        popUpTo(Screen.Home.route) {
            inclusive = false
        }
    }
}
