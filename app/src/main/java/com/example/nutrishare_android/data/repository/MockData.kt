package com.example.nutrishare_android.data.repository

import com.example.nutrishare_android.data.model.Address
import com.example.nutrishare_android.data.model.CartItem
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
import com.example.nutrishare_android.data.model.UpdateProfileRequest
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

    private val products = listOf(
        Product(1L, "Organic Eggs 10 Pack", 6900, null, "Groceries", 50, "Fresh organic eggs."),
        Product(2L, "Seasonal Strawberries 500g", 9900, null, "Fruit", 30, "Sweet strawberries."),
        Product(3L, "Beef Bulgogi 300g", 15900, null, "Meat", 20, "Ready-to-cook bulgogi."),
        Product(4L, "Greek Yogurt 400g", 7800, null, "Dairy", 40, "Plain greek yogurt.")
    )

    private val groups = mutableListOf(
        Group(101L, "Strawberry Group Buy", "Seasonal Strawberries 500g", 7900, 10, 6, LocalDateTime.now().plusDays(2).format(dateTimeFmt), "OPEN"),
        Group(102L, "Bulgogi Group Buy", "Beef Bulgogi 300g", 12900, 8, 8, LocalDateTime.now().minusDays(1).format(dateTimeFmt), "CLOSED")
    )

    private val cartItems = mutableListOf(
        CartItem(1L, "Organic Eggs 10 Pack", 6900, 1, 6900),
        CartItem(2L, "Seasonal Strawberries 500g", 9900, 2, 19800)
    )

    private val myOrdersList = mutableListOf(
        Order(501L, "Organic Eggs 10 Pack x1", 26800, "PAID", LocalDateTime.now().minusDays(3).format(dateTimeFmt))
    )

    private var myProfile = User(
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

    private val myParticipationsList = mutableListOf(
        Participation(
            groupPurchaseId = 101L,
            title = "Strawberry Group Buy",
            productName = "Seasonal Strawberries 500g",
            currentQuantity = 6,
            targetQuantity = 10,
            status = "OPEN"
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

    fun createGroup(request: CreateGroupRequest): Result<Unit> {
        val product = products.find { it.id == request.productId }
        val newGroup = Group(
            id = Random.nextLong(1000, 9999),
            title = request.title,
            productName = product?.name ?: "Custom Product",
            typePrice = request.unitPrice,
            targetQuantity = request.targetQuantity,
            currentQuantity = 1,
            dueDate = request.endAt,
            status = "OPEN"
        )
        groups.add(0, newGroup)
        myParticipationsList.add(
            0,
            Participation(
                groupPurchaseId = newGroup.id,
                title = newGroup.title,
                productName = newGroup.productName,
                currentQuantity = newGroup.currentQuantity,
                targetQuantity = newGroup.targetQuantity,
                status = newGroup.status
            )
        )
        return Result.success(Unit)
    }

    fun joinGroup(id: Long, request: JoinGroupRequest): Result<Unit> {
        val index = groups.indexOfFirst { it.id == id }
        if (index < 0) {
            return Result.failure(IllegalArgumentException("Group not found: $id"))
        }

        val group = groups[index]
        val nextQuantity = group.currentQuantity + request.quantity
        val nextStatus = if (nextQuantity >= group.targetQuantity) "CLOSED" else group.status
        val updated = group.copy(currentQuantity = nextQuantity, status = nextStatus)
        groups[index] = updated

        val participationIndex = myParticipationsList.indexOfFirst { it.groupPurchaseId == id }
        val participation = Participation(
            groupPurchaseId = updated.id,
            title = updated.title,
            productName = updated.productName,
            currentQuantity = updated.currentQuantity,
            targetQuantity = updated.targetQuantity,
            status = updated.status
        )
        if (participationIndex >= 0) {
            myParticipationsList[participationIndex] = participation
        } else {
            myParticipationsList.add(0, participation)
        }

        return Result.success(Unit)
    }

    fun myProfile(): Result<User> = Result.success(myProfile)

    fun updateMyProfile(request: UpdateProfileRequest): Result<Unit> {
        myProfile = myProfile.copy(
            nickname = request.nickname,
            address = Address(
                zipCode = request.zipCode,
                addressLine1 = request.addressLine1,
                addressLine2 = request.addressLine2
            )
        )
        return Result.success(Unit)
    }

    fun currentMyOrders(): Result<List<Order>> = Result.success(myOrdersList.toList())

    fun myParticipations(): Result<List<Participation>> = Result.success(myParticipationsList.toList())

    fun token(): Result<String> = Result.success("mock-token")
    fun unit(): Result<Unit> = Result.success(Unit)
}
