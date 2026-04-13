package com.example.infrastructure.dto.requests.order

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull


data class OrderCreateRequest(
    @field:NotNull(message = "userId обязателен")
    val userId: Long,

    @field:NotEmpty(message = "Заказ должен содержать хотя бы одно блюдо")
    val dishIds: List<Long>
)