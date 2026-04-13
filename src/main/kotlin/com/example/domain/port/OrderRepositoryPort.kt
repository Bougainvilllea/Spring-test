package com.example.domain.port

import com.example.domain.port.BaseRepositoryPort
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus

interface OrderRepositoryPort : BaseRepositoryPort<Order> {
    fun findAllByUserId(userId: Long): List<Order>
    fun findAllByStatus(status: OrderStatus): List<Order>
    fun findAllByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>
    fun findAllByDishId(dishId: Long): List<Order>
    fun existsOrderWithDish(dishId: Long): Boolean
}

