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
import com.example.nutrishare_android.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutriRepositoryImpl @Inject constructor(
    private val api: ApiService
) : NutriRepository {
    private suspend fun <T> withMock(
        mock: () -> Result<T>,
        apiCall: suspend () -> Result<T>
    ): Result<T> {
        if (MockDataConfig.forceMock) return mock()
        return try {
            val apiResult = apiCall()
            if (apiResult.isSuccess || !MockDataConfig.fallbackToMockOnError) {
                apiResult
            } else {
                mock()
            }
        } catch (e: Exception) {
            if (MockDataConfig.fallbackToMockOnError) mock() else Result.failure(e)
        }
    }

    override suspend fun getProducts(size: Int, page: Int): Result<PageResponse<Product>> {
        return withMock(
            mock = { MockData.productsPage(size, page) },
            apiCall = { api.getProducts(size = size, page = page).toResult() }
        )
    }

    override suspend fun searchProducts(
        query: String,
        size: Int
    ): Result<PageResponse<Product>> {
        return withMock(
            mock = { MockData.searchProducts(query, size) },
            apiCall = { api.searchProducts(query = query, size = size).toResult() }
        )
    }

    override suspend fun getProductDetail(id: Long): Result<Product> {
        return withMock(
            mock = { MockData.productDetail(id) },
            apiCall = { api.getProductDetail(id).toResult() }
        )
    }

    override suspend fun getCart(): Result<CartResponse> {
        return withMock(
            mock = { MockData.cart() },
            apiCall = { api.getCart().toResult() }
        )
    }

    override suspend fun addToCart(request: AddToCartRequest): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.addToCart(request).toUnitResult() }
        )
    }

    override suspend fun updateCartItem(
        productId: Long,
        request: UpdateCartRequest
    ): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.updateCartItem(productId, request).toUnitResult() }
        )
    }

    override suspend fun removeCartItem(productId: Long): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.removeCartItem(productId).toUnitResult() }
        )
    }

    override suspend fun createOrder(
        request: CreateOrderRequest
    ): Result<ResourceIdResponse> {
        return withMock(
            mock = { MockData.orderCreated() },
            apiCall = { api.createOrder(request).toResult() }
        )
    }

    override suspend fun getGroups(size: Int): Result<PageResponse<Group>> {
        return withMock(
            mock = { MockData.groupsPage(size) },
            apiCall = { api.getGroups(size).toResult() }
        )
    }

    override suspend fun getGroupDetail(id: Long): Result<Group> {
        return withMock(
            mock = { MockData.groupDetail(id) },
            apiCall = { api.getGroupDetail(id).toResult() }
        )
    }

    override suspend fun createGroup(request: CreateGroupRequest): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.createGroup(request).toUnitResult() }
        )
    }

    override suspend fun joinGroup(id: Long, request: JoinGroupRequest): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.joinGroup(id, request).toUnitResult() }
        )
    }

    override suspend fun getMyProfile(): Result<User> {
        return withMock(
            mock = { MockData.myProfile() },
            apiCall = { api.getMyProfile().toResult() }
        )
    }

    override suspend fun updateMyProfile(request: UpdateProfileRequest): Result<Unit> {
        return withMock(
            mock = { MockData.unit() },
            apiCall = { api.updateMyProfile(request).toUnitResult() }
        )
    }

    override suspend fun getMyOrders(): Result<List<Order>> {
        return withMock(
            mock = { MockData.myOrders() },
            apiCall = { api.getMyOrders().toResult() }
        )
    }

    override suspend fun getMyParticipations(): Result<List<Participation>> {
        return withMock(
            mock = { MockData.myParticipations() },
            apiCall = { api.getMyParticipations().toResult() }
        )
    }
}
