package domain.model

import org.example.example.domain.model.OrderStatus
import java.time.LocalDateTime

data class Order(
    var id: Long? = null,
    val userId: Long,
    var status: OrderStatus,
    val createdAt: LocalDateTime,
    val dishes: List<Dish>
) {
    init {
        require(dishes.isNotEmpty()) { "Order must contain at least one dish" }
    }

    fun updateStatus(newStatus: OrderStatus): Order {
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

}