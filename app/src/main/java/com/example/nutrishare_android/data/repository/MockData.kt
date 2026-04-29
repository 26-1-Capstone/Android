package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.model.Address
import com.example.nutrishare_android.data.model.CartItem
import com.example.nutrishare_android.data.model.CartResponse
import com.example.nutrishare_android.data.model.CreateOrderRequest
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
}

object MockData {
    private val dateTimeFmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private val products = listOf(
        Product(
            id = 1L,
            name = "Organic Eggs 10 Pack",
            price = 6900,
            imageUrl = null,
            categoryName = "Groceries",
            stockQuantity = 50,
            description = "Fresh organic eggs."
        ),
        Product(
            id = 2L,
            name = "Seasonal Strawberries 500g",
            price = 9900,
            imageUrl = null,
            categoryName = "Fruit",
            stockQuantity = 30,
            description = "Sweet strawberries."
        ),
        Product(
            id = 3L,
            name = "Beef Bulgogi 300g",
            price = 15900,
            imageUrl = null,
            categoryName = "Meat",
            stockQuantity = 20,
            description = "Ready-to-cook bulgogi."
        ),
        Product(
            id = 4L,
            name = "Greek Yogurt 400g",
            price = 7800,
            imageUrl = null,
            categoryName = "Dairy",
            stockQuantity = 40,
            description = "Plain greek yogurt."
        )
    )

    private val groups = listOf(
        Group(
            id = 101L,
            title = "Strawberry Group Buy",
            productName = "Seasonal Strawberries 500g",
            typePrice = 7900,
            targetQuantity = 10,
            currentQuantity = 6,
            dueDate = LocalDateTime.now().plusDays(2).format(dateTimeFmt),
            status = "OPEN"
        ),
        Group(
            id = 102L,
            title = "Bulgogi Group Buy",
            productName = "Beef Bulgogi 300g",
            typePrice = 12900,
            targetQuantity = 8,
            currentQuantity = 8,
            dueDate = LocalDateTime.now().minusDays(1).format(dateTimeFmt),
            status = "CLOSED"
        )
    )

    private val cartItems = mutableListOf(
        CartItem(
            productId = 1L,
            productName = "Organic Eggs 10 Pack",
            typePrice = 6900,
            quantity = 1,
            totalPrice = 6900
        ),
        CartItem(
            productId = 2L,
            productName = "Seasonal Strawberries 500g",
            typePrice = 9900,
            quantity = 2,
            totalPrice = 19800
        )
    )

    private val myOrdersList = mutableListOf(
        Order(
            orderId = 501L,
            summary = "Organic Eggs 10 Pack x1",
            totalAmount = 26800,
            status = "PAID",
            orderDate = LocalDateTime.now().minusDays(3).format(dateTimeFmt)
        )
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
        return Result.success(
            PageResponse(
                content = filtered.take(size),
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
        return Result.success(
            CartResponse(
                items = items,
                totalAmount = items.sumOf { it.totalPrice }
            )
        )
    }

    fun addToCart(productId: Long, quantity: Int): Result<Unit> {
        val product = products.find { it.id == productId }
            ?: return Result.failure(IllegalArgumentException("Product not found: $productId"))

        val index = cartItems.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val existing = cartItems[index]
            val nextQuantity = existing.quantity + quantity
            cartItems[index] = existing.copy(
                quantity = nextQuantity,
                totalPrice = existing.typePrice * nextQuantity
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
        val index = cartItems.indexOfFirst { it.productId == productId }
        if (index < 0) {
            return Result.failure(IllegalArgumentException("Cart item not found: $productId"))
        }

        if (quantity <= 0) {
            cartItems.removeAt(index)
        } else {
            val existing = cartItems[index]
            cartItems[index] = existing.copy(
                quantity = quantity,
                totalPrice = existing.typePrice * quantity
            )
        }
        return Result.success(Unit)
    }

    fun removeCartItem(productId: Long): Result<Unit> {
        cartItems.removeAll { it.productId == productId }
        return Result.success(Unit)
    }

    fun orderCreated(request: CreateOrderRequest): Result<ResourceIdResponse> {
        val orderId = Random.nextLong(1000, 9999)
        myOrdersList.add(
            0,
            Order(
                orderId = orderId,
                summary = request.items.joinToString(", ") { "${it.productName} x${it.quantity}" },
                totalAmount = request.items.sumOf { it.unitPrice * it.quantity },
                status = "PAID",
                orderDate = LocalDateTime.now().format(dateTimeFmt)
            )
        )
        return Result.success(ResourceIdResponse(resourceId = orderId))
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
                nickname = "Test User",
                email = "test@example.com",
                profileImageUrl = null,
                totalSavings = 125000,
                address = Address(
                    zipCode = "06236",
                    addressLine1 = "123 Teheran-ro, Gangnam-gu",
                    addressLine2 = "101-1001"
                )
            )
        )
    }

    fun myOrders(): Result<List<Order>> = Result.success(myOrdersList.toList())

    fun currentMyOrders(): Result<List<Order>> = Result.success(myOrdersList.toList())

    fun myParticipations(): Result<List<Participation>> {
        return Result.success(
            listOf(
                Participation(
                    groupPurchaseId = 101L,
                    title = "Strawberry Group Buy",
                    productName = "Seasonal Strawberries 500g",
                    currentQuantity = 6,
                    targetQuantity = 10,
                    status = "OPEN"
                )
            )
        )
    }

    fun deleteMyAccount(): Result<Unit> = Result.success(Unit)

    fun token(): Result<String> = Result.success("mock-token")
    fun unit(): Result<Unit> = Result.success(Unit)
}
