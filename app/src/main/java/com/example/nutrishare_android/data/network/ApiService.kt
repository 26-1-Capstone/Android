package com.example.nutrishare_android.data.network

import com.example.nutrishare_android.data.model.*
import retrofit2.Response
import retrofit2.http.*

// frontend api.js의 모든 엔드포인트 대응
// baseURL: /api/v1  (frontend VITE_API_BASE_URL과 동일)
interface ApiService {

    // ─── 인증 ───
    @GET("auth/dev-login")
    suspend fun devLogin(): Response<ApiResponse<String>>

    @POST("auth/reissue")
    suspend fun reissueToken(): Response<ApiResponse<String>>

    // ─── 상품 ───
    @GET("products")
    suspend fun getProducts(
        @Query("size") size: Int = 10,
        @Query("page") page: Int = 0
    ): Response<ApiResponse<PageResponse<Product>>>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<Product>>>

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<ApiResponse<Product>>

    // ─── 장바구니 ───
    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartResponse>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<Unit>>

    @PUT("cart/{productId}")
    suspend fun updateCartItem(
        @Path("productId") productId: Long,
        @Body request: UpdateCartRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("cart/{productId}")
    suspend fun removeCartItem(@Path("productId") productId: Long): Response<ApiResponse<Unit>>

    // ─── 주문 ───
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<ResourceIdResponse>>

    // ─── 공동구매 ───
    @GET("groups")
    suspend fun getGroups(
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<Group>>>

    @GET("groups/{id}")
    suspend fun getGroupDetail(@Path("id") id: Long): Response<ApiResponse<Group>>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<ApiResponse<Unit>>

    @POST("groups/{id}/join")
    suspend fun joinGroup(
        @Path("id") id: Long,
        @Body request: JoinGroupRequest
    ): Response<ApiResponse<Unit>>

    // ─── 사용자 ───
    @GET("users/me")
    suspend fun getMyProfile(): Response<ApiResponse<User>>

    @PUT("users/me")
    suspend fun updateMyProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<Unit>>

    @GET("users/me/orders")
    suspend fun getMyOrders(): Response<ApiResponse<List<Order>>>

    @GET("users/me/participations")
    suspend fun getMyParticipations(): Response<ApiResponse<List<Participation>>>
}
