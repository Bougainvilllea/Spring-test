package domain.port

import domain.model.Order
import org.example.example.domain.model.OrderStatus

interface OrderRepositoryPort {
    fun findAllByUserId(userId: Long): List<Order>
    fun findAllByStatus(status: OrderStatus): List<Order>
    fun findAllByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>
    fun findAllByDishId(dishId: Long): List<Order>
    fun existsOrderWithDish(dishId: Long): Boolean
    fun findAll(): List<Order>
    fun findById(id: Long): Order?
    fun update(entity: Order): Order
    fun deleteById(id: Long): Boolean
    fun create(entity: Order): Order
}

