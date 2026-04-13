package com.example.infrastructure.dto.response.restaurant
import com.example.infrastructure.dto.response.DishResponse

data class OrderResponse(
    val id: Long?,
    val userId: Long,
    val status: String,
    val createdAt: String,
    val dishes: List<DishResponse>
)