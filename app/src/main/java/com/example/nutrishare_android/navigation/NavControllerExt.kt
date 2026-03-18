package com.example.nutrishare_android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateToTopLevel(route: String) {
    val isCurrentDestination = currentDestination?.hierarchy?.any { it.route == route } == true
    if (isCurrentDestination) return

    if (popBackStack(route, inclusive = false, saveState = true)) {
        return
    }

    navigate(route) {
        launchSingleTop = true
        restoreState = true

        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}
