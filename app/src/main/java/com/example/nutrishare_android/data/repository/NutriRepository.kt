package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.model.AddToCartRequest
import com.example.nutrishare_android.data.model.CartResponse
import com.example.nutrishare_android.data.model.CreateGroupRequest
import com.example.nutrishare_android.data.model.CreateOrderRequest
import com.example.nutrishare_android.data.model.Group
import com.example.nutrishare_android.data.model.JoinGroupRequest
import com.example.nutrishare_android.data.model.Order
import com.example.nutrishare_android.data.model.PageResponse
import com.example.nutrishare_android.data.model.Participation
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.model.ResourceIdResponse
import com.example.nutrishare_android.data.model.UpdateCartRequest
import com.example.nutrishare_android.data.model.UpdateProfileRequest
import com.example.nutrishare_android.data.model.User

interface NutriRepository {
    suspend fun getProducts(size: Int = 10, page: Int = 0): Result<PageResponse<Product>>
    suspend fun searchProducts(query: String, size: Int = 50): Result<PageResponse<Product>>
    suspend fun getProductDetail(id: Long): Result<Product>

    suspend fun getCart(): Result<CartResponse>
    suspend fun addToCart(request: AddToCartRequest): Result<Unit>
    suspend fun updateCartItem(productId: Long, request: UpdateCartRequest): Result<Unit>
    suspend fun removeCartItem(productId: Long): Result<Unit>

    suspend fun createOrder(request: CreateOrderRequest): Result<ResourceIdResponse>

    suspend fun getGroups(size: Int = 50): Result<PageResponse<Group>>
    suspend fun getGroupDetail(id: Long): Result<Group>
    suspend fun createGroup(request: CreateGroupRequest): Result<Unit>
    suspend fun joinGroup(id: Long, request: JoinGroupRequest): Result<Unit>

    suspend fun getMyProfile(): Result<User>
    suspend fun updateMyProfile(request: UpdateProfileRequest): Result<Unit>
    suspend fun getMyOrders(): Result<List<Order>>
    suspend fun getMyParticipations(): Result<List<Participation>>
}
