package com.example.nutrishare_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nutrishare_android.BuildConfig
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.ui.components.LoadingScreen
import com.example.nutrishare_android.ui.screen.CartScreen
import com.example.nutrishare_android.ui.screen.CheckoutScreen
import com.example.nutrishare_android.ui.screen.GroupCreateScreen
import com.example.nutrishare_android.ui.screen.GroupDetailScreen
import com.example.nutrishare_android.ui.screen.GroupListScreen
import com.example.nutrishare_android.ui.screen.HomeScreen
import com.example.nutrishare_android.ui.screen.LoginScreen
import com.example.nutrishare_android.ui.screen.MyPageScreen
import com.example.nutrishare_android.ui.screen.OrderCompleteScreen
import com.example.nutrishare_android.ui.screen.ProductDetailScreen
import com.example.nutrishare_android.ui.screen.ProfileEditScreen
import com.example.nutrishare_android.ui.screen.SearchScreen
import com.example.nutrishare_android.ui.screen.SplashScreen
import com.example.nutrishare_android.ui.viewmodel.CheckoutItem

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            RequireAuthentication(navController = navController) {
                HomeScreen(navController = navController)
            }
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.GroupList.route) {
            GroupListScreen(navController = navController)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            ProductDetailScreen(navController = navController, productId = id)
        }

        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            GroupDetailScreen(navController = navController, groupId = id)
        }

        composable(Screen.Cart.route) {
            RequireAuthentication(navController = navController) {
                CartScreen(navController = navController)
            }
        }
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("quantity") { type = NavType.IntType; defaultValue = 1 }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1

            val itemsInHandle = backStackEntry.savedStateHandle.get<List<CheckoutItem>>("checkoutItems")
            if (BuildConfig.DEBUG) {
                android.util.Log.d(
                    "CheckoutLog",
                    "NavGraph checkoutItems size: ${itemsInHandle?.size ?: "null"}"
                )
            }

            RequireAuthentication(navController = navController) {
                CheckoutScreen(
                    navController = navController,
                    productId = if (productId == -1L) null else productId,
                    quantity = quantity,
                    viewModel = hiltViewModel(backStackEntry)
                )
            }
        }
        composable(Screen.MyPage.route) {
            RequireAuthentication(navController = navController) {
                MyPageScreen(navController = navController)
            }
        }
        composable(Screen.ProfileEdit.route) {
            RequireAuthentication(navController = navController) {
                ProfileEditScreen(navController = navController)
            }
        }
        composable(Screen.GroupCreate.route) {
            RequireAuthentication(navController = navController) {
                GroupCreateScreen(navController = navController)
            }
        }

        composable(
            route = Screen.OrderComplete.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            OrderCompleteScreen(navController = navController, orderId = id)
        }
    }
}

@Composable
private fun RequireAuthentication(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val sessionState = AuthStorage.sessionState.collectAsStateWithLifecycle().value
    val canAccess = sessionState.canAccessProtectedRoutes

    LaunchedEffect(canAccess, navController) {
        if (!canAccess) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (canAccess) {
        content()
    } else {
        LoadingScreen()
    }
}
