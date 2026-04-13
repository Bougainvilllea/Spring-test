package com.example.infrastructure.adapter.controller

import jakarta.validation.Valid
import com.example.domain.model.OrderStatus
import com.example.domain.service.OrderService
import com.example.infrastructure.dto.requests.order.OrderCreateRequest
import com.example.infrastructure.dto.requests.order.OrderStatusUpdateRequest
import com.example.infrastructure.dto.response.restaurant.OrderResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.shared.utils.mapper.OrderMapper

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderMapper: OrderMapper
) {

    @GetMapping
    fun listOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<List<OrderResponse>> {
        val orderStatus = status?.let { OrderStatus.valueOf(it.uppercase()) }
        val orders = orderService.getAllOrders(userId, orderStatus)
        val response = orders.map { orderMapper.toResponse(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createOrder(@Valid @RequestBody createRequest: OrderCreateRequest): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(createRequest.userId, createRequest.dishIds)
        val response = orderMapper.toResponse(order)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = orderService.getOrderById(id)
        val response = orderMapper.toResponse(order)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody statusUpdateRequest: OrderStatusUpdateRequest
    ): ResponseEntity<OrderResponse> {
        val newStatus = OrderStatus.valueOf(statusUpdateRequest.status.uppercase())
        val updatedOrder = orderService.updateOrderStatus(id, newStatus)
        val response = orderMapper.toResponse(updatedOrder)
        return ResponseEntity.ok(response)
    }
}