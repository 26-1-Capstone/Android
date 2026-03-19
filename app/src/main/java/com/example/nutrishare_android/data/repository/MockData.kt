package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.model.Address
import com.example.nutrishare_android.data.model.CartItem
import com.example.nutrishare_android.data.model.CartResponse
import com.example.nutrishare_android.data.model.Group
import com.example.nutrishare_android.data.model.Order
import com.example.nutrishare_android.data.model.PageResponse
import com.example.nutrishare_android.data.model.Participation
import com.example.nutrishare_android.data.model.Product
import com.example.nutrishare_android.data.model.ResourceIdResponse
import com.example.nutrishare_android.data.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object MockDataConfig {
    @Volatile var forceMock: Boolean = false
    @Volatile var fallbackToMockOnError: Boolean = true
}

object MockData {
    private val dateTimeFmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private val products: List<Product> = listOf(
        Product(
            id = 1L,
            name = "유기농 계란 10구",
            price = 6900,
            imageUrl = null,
            categoryName = "축산",
            stockQuantity = 50,
            description = "신선한 유기농 계란입니다."
        ),
        Product(
            id = 2L,
            name = "제철 딸기 500g",
            price = 9900,
            imageUrl = null,
            categoryName = "과일",
            stockQuantity = 30,
            description = "달콤한 제철 딸기입니다."
        ),
        Product(
            id = 3L,
            name = "한우 불고기 300g",
            price = 15900,
            imageUrl = null,
            categoryName = "정육",
            stockQuantity = 20,
            description = "부드러운 한우 불고기입니다."
        ),
        Product(
            id = 4L,
            name = "그릭 요거트 400g",
            price = 7800,
            imageUrl = null,
            categoryName = "유제품",
            stockQuantity = 40,
            description = "담백한 그릭 요거트입니다."
        )
    )

    private val groups: List<Group> = listOf(
        Group(
            id = 101L,
            title = "딸기 공동구매 10명 모집",
            productName = "제철 딸기 500g",
            typePrice = 7900,
            targetQuantity = 10,
            currentQuantity = 6,
            dueDate = LocalDateTime.now().plusDays(2).format(dateTimeFmt),
            status = "OPEN"
        ),
        Group(
            id = 102L,
            title = "한우 불고기 공동구매",
            productName = "한우 불고기 300g",
            typePrice = 12900,
            targetQuantity = 8,
            currentQuantity = 8,
            dueDate = LocalDateTime.now().minusDays(1).format(dateTimeFmt),
            status = "CLOSED"
        )
    )

    private val cartItems = mutableListOf(
        CartItem(productId = 1L, productName = "유기농 계란 10구", typePrice = 6900, quantity = 1, totalPrice = 6900),
        CartItem(productId = 2L, productName = "제철 딸기 500g", typePrice = 9900, quantity = 2, totalPrice = 19800)
    )

    fun productsPage(size: Int, page: Int): Result<PageResponse<Product>> {
        val from = (page * size).coerceAtMost(products.size)
        val to = (from + size).coerceAtMost(products.size)
        val content = products.subList(from, to)
        val totalPages = if (size == 0) 1 else ((products.size + size - 1) / size)
        return Result.success(
            PageResponse(
                content = content,
                totalElements = products.size.toLong(),
                totalPages = totalPages,
                last = page >= totalPages - 1
            )
        )
    }

    fun searchProducts(query: String, size: Int): Result<PageResponse<Product>> {
        val filtered = products.filter { it.name.contains(query, ignoreCase = true) }
        val content = filtered.take(size)
        return Result.success(
            PageResponse(
                content = content,
                totalElements = filtered.size.toLong(),
                totalPages = 1,
                last = true
            )
        )
    }

    fun productDetail(id: Long): Result<Product> {
        val product = products.find { it.id == id } ?: products.first()
        return Result.success(product)
    }

    fun cart(): Result<CartResponse> {
        val items = cartItems.map { it.copy() }
        val total = items.sumOf { it.totalPrice }
        return Result.success(CartResponse(items = items, totalAmount = total))
    }

    fun addToCart(productId: Long, quantity: Int): Result<Unit> {
        val product = products.find { it.id == productId }
            ?: return Result.failure(IllegalArgumentException("Product not found: $productId"))

        val existingIndex = cartItems.indexOfFirst { it.productId == productId }
        if (existingIndex >= 0) {
            val existingItem = cartItems[existingIndex]
            val nextQuantity = existingItem.quantity + quantity
            cartItems[existingIndex] = existingItem.copy(
                quantity = nextQuantity,
                totalPrice = existingItem.typePrice * nextQuantity
            )
        } else {
            cartItems += CartItem(
                productId = product.id,
                productName = product.name,
                typePrice = product.price,
                quantity = quantity,
                totalPrice = product.price * quantity
            )
        }
        return Result.success(Unit)
    }

    fun updateCartItem(productId: Long, quantity: Int): Result<Unit> {
        val existingIndex = cartItems.indexOfFirst { it.productId == productId }
        if (existingIndex < 0) {
            return Result.failure(IllegalArgumentException("Cart item not found: $productId"))
        }

        if (quantity <= 0) {
            cartItems.removeAt(existingIndex)
        } else {
            val existingItem = cartItems[existingIndex]
            cartItems[existingIndex] = existingItem.copy(
                quantity = quantity,
                totalPrice = existingItem.typePrice * quantity
            )
        }
        return Result.success(Unit)
    }

    fun removeCartItem(productId: Long): Result<Unit> {
        cartItems.removeAll { it.productId == productId }
        return Result.success(Unit)
    }

    fun orderCreated(): Result<ResourceIdResponse> {
        return Result.success(ResourceIdResponse(resourceId = Random.nextLong(1000, 9999)))
    }

    fun groupsPage(size: Int): Result<PageResponse<Group>> {
        val content = groups.take(size)
        return Result.success(
            PageResponse(
                content = content,
                totalElements = groups.size.toLong(),
                totalPages = 1,
                last = true
            )
        )
    }

    fun groupDetail(id: Long): Result<Group> {
        val group = groups.find { it.id == id } ?: groups.first()
        return Result.success(group)
    }

    fun myProfile(): Result<User> {
        return Result.success(
            User(
                nickname = "테스트유저",
                email = "test@example.com",
                profileImageUrl = null,
                totalSavings = 125000,
                address = Address(
                    zipCode = "06236",
                    addressLine1 = "서울 강남구 테헤란로 123",
                    addressLine2 = "101동 1001호"
                )
            )
        )
    }

    fun myOrders(): Result<List<Order>> {
        return Result.success(
            listOf(
                Order(
                    orderId = 501L,
                    summary = "유기농 계란 10구 외 1건",
                    totalAmount = 26800,
                    status = "PAID",
                    orderDate = LocalDateTime.now().minusDays(3).format(dateTimeFmt)
                )
            )
        )
    }

    fun myParticipations(): Result<List<Participation>> {
        return Result.success(
            listOf(
                Participation(
                    groupPurchaseId = 101L,
                    title = "딸기 공동구매 10명 모집",
                    productName = "제철 딸기 500g",
                    currentQuantity = 6,
                    targetQuantity = 10,
                    status = "OPEN"
                )
            )
        )
    }

    fun token(): Result<String> = Result.success("mock-token")
    fun unit(): Result<Unit> = Result.success(Unit)
}
