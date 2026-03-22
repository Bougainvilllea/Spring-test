package org.example.example.infrastructure.dto.response.restaurant
import infrastructure.dto.response.DishResponse

data class OrderResponse(
    val id: Long?,
    val userId: Long,
    val status: String,
    val createdAt: String,
    val dishes: List<DishResponse>
)