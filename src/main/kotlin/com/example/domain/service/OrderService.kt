package com.example.domain.service

import com.example.domain.model.Order
import com.example.domain.model.OrderStatus

interface OrderService {
    fun getOrderById(orderId: Long): Order
    fun getAllOrders(userId: Long?, status: OrderStatus?): List<Order>
    fun createOrder(userId: Long, dishIds: List<Long>): Order
    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Order
}