package com.example.nutrishare_android.navigation

// frontend App.jsx의 Route path들과 1:1 대응
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object GroupList : Screen("groups")
    object GroupCreate : Screen("groups/new")
    object Cart : Screen("cart")
    object MyPage : Screen("mypage")
    object ProfileEdit : Screen("mypage/edit")

    // 파라미터가 있는 라우트
    object ProductDetail : Screen("products/{id}") {
        fun createRoute(id: Long) = "products/$id"
    }
    object GroupDetail : Screen("groups/{id}") {
        fun createRoute(id: Long) = "groups/$id"
    }
    object OrderComplete : Screen("orders/{id}/complete") {
        fun createRoute(id: Long) = "orders/$id/complete"
    }

    object Checkout : Screen("checkout?productId={productId}&quantity={quantity}") {
        fun createRoute(productId: Long? = null, quantity: Int? = null): String {
            return if (productId != null && quantity != null) {
                "checkout?productId=$productId&quantity=$quantity"
            } else {
                "checkout"
            }
        }
    }

}
