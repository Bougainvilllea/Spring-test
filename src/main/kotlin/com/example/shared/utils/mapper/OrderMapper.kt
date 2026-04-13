package com.example.shared.utils.mapper


import com.example.domain.model.Order
import com.example.infrastructure.dto.response.restaurant.OrderResponse
import org.springframework.stereotype.Component

@Component
class OrderMapper(
    private val dishMapper: DishMapper
) {

    fun toResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id,
            userId = order.userId,
            status = order.status.name,
            createdAt = order.createdAt.toString(),
            dishes = order.dishes.map { dishMapper.toResponse(it) }
        )
    }
}