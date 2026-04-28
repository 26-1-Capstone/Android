package com.example.nutrishare_android.data.model

import com.google.gson.annotations.SerializedName

// ─── 공통 API 응답 래퍼 ───
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
// ─── 상품 ───
data class Product(
    val id: Long,
    val name: String,
    val price: Long,
    @SerializedName("imageUrl") val imageUrl: String?,
    val categoryName: String?,
    val stockQuantity: Int,
    val description: String?
)

// ─── 공동구매 그룹 ───
data class Group(
    val id: Long,
    val title: String,
    val productName: String,
    @SerializedName("typePrice") val typePrice: Long,
    val targetQuantity: Int,
    val currentQuantity: Int,
    val dueDate: String?,
    val status: String?
)

// ─── 장바구니 ───
data class CartItem(
    val productId: Long,
    val productName: String,
    @SerializedName("typePrice") val typePrice: Long,
    val quantity: Int,
    val totalPrice: Long
)

data class CartResponse(
    val items: List<CartItem>,
    val totalAmount: Long
)

// ─── 주문 ───
data class Order(
    val orderId: Long,
    val summary: String?,
    val totalAmount: Long?,
    val status: String?,
    @SerializedName("orderDate")
    val orderDate: String?
)

data class ShippingAddress(
    val zipCode: String,
    val line1: String,
    val line2: String
)

data class OrderItem(
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Int
)

data class CreateOrderRequest(
    val shippingAddress: ShippingAddress,
    val items: List<OrderItem>
)

data class ResourceIdResponse(
    val resourceId: Long
)

// ─── 사용자 ───
data class Address(
    val zipCode: String?,
    val addressLine1: String?,
    val addressLine2: String?
)

data class User(
    val nickname: String,
    val email: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    val totalSavings: Long?,
    val address: Address?
)

data class UpdateProfileRequest(
    val nickname: String,
    val zipCode: String,
    val addressLine1: String,
    val addressLine2: String
)

// ─── 공동구매 참여 (MyPage) ───
data class Participation(
    val groupPurchaseId: Long,
    val title: String?,
    val productName: String?,
    val currentQuantity: Int,
    val targetQuantity: Int,
    val status: String?
)

// ─── 인증 ───
data class TokenResponse(val data: String?)

data class CreateGroupRequest(
    val productId: Long,
    val title: String,
    val targetQuantity: Int,
    val unitPrice: Long,
    val endAt: String
)

data class JoinGroupRequest(val quantity: Int)

data class AddToCartRequest(val productId: Long, val quantity: Int)
data class UpdateCartRequest(val quantity: Int)
