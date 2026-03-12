package com.example.nutrishare_android.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nutrishare_android.data.local.AuthStorage
import com.example.nutrishare_android.ui.screen.*

// frontend App.jsx의 <Routes> 전체 구조를 NavHost로 변환
@Composable
fun NavGraph(navController: NavHostController, context: Context) {
    val authStorage = AuthStorage(context)
    val startDestination = if (authStorage.isAuthenticated()) Screen.Home.route else Screen.Login.route
    val hello: String = "hello"

    NavHost(navController = navController, startDestination = startDestination) {

        // 공개 라우트
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, context = context)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.GroupList.route) {
            GroupListScreen(navController = navController)
        }

        // /products/:id
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            ProductDetailScreen(navController = navController, productId = id, context = context)
        }

        // /groups/:id
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            GroupDetailScreen(navController = navController, groupId = id)
        }

        // 인증 필요 라우트 (isAuthenticated 체크는 각 Screen에서 처리)
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
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

            // 🔍 [추가] 여기서 주머니를 직접 열어서 로그를 찍어봅니다.
            val itemsInHandle = backStackEntry.savedStateHandle.get<List<com.example.nutrishare_android.ui.viewmodel.CheckoutItem>>("checkoutItems")
            android.util.Log.d("CheckoutLog", "NavGraph에서 직접 확인한 보따리 개수: ${itemsInHandle?.size ?: "null"}")

            CheckoutScreen(
                navController = navController,
                productId = if (productId == -1L) null else productId,
                quantity = quantity,
                context = context,
                // backStackEntry를 명시적으로 전달하여 뷰모델이 이 주머니를 쓰게 강제합니다.
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    viewModelStoreOwner = backStackEntry
                )
            )
        }
        composable(Screen.MyPage.route) {
            MyPageScreen(navController = navController, context = context)
        }
        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(navController = navController)
        }
        composable(Screen.GroupCreate.route) {
            GroupCreateScreen(navController = navController)
        }

        // /orders/:id/complete
        composable(
            route = Screen.OrderComplete.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            OrderCompleteScreen(navController = navController, orderId = id)
        }
    }
}
