package com.example.domain.model

import com.example.domain.model.Dish
import com.example.domain.model.common.IHaveID
import java.time.LocalDateTime

data class Order(
    override var id: Long? = null,
    val userId: Long,
    var status: OrderStatus,
    val createdAt: LocalDateTime,
    val dishes: List<Dish>
) : IHaveID<Order> {
    init {
        require(dishes.isNotEmpty()) { "Order must contain at least one dish" }
    }

    fun updateStatus(newStatus: OrderStatus): Order {
        // Валидация переходов статусов
        when (status) {
            OrderStatus.PENDING -> {
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw IllegalStateException("Cannot change order status from ${status.name} to ${newStatus.name}")
                }
            }
            OrderStatus.CONFIRMED -> {
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw IllegalStateException("Cannot change order status from ${status.name} to ${newStatus.name}")
                }
            }
            OrderStatus.DELIVERED -> {
                throw IllegalStateException("Cannot change status of already delivered order")
            }
            OrderStatus.CANCELLED -> {
                throw IllegalStateException("Cannot change status of cancelled order")
            }
        }

        return this.copy(status = newStatus)
    }

    override fun withId(id: Long): Order {
        return this.copy(id = id)
    }
}