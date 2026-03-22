package org.example.example.domain.service

import domain.model.Order
import org.example.example.domain.model.OrderStatus

interface OrderService {
    fun getOrderById(orderId: Long): Order
    fun getAllOrders(userId: Long?, status: OrderStatus?): List<Order>
    fun createOrder(userId: Long, dishIds: List<Long>): Order
    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Order
}