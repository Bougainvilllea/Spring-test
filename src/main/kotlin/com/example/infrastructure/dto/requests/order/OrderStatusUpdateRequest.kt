package com.example.infrastructure.dto.requests.order

import jakarta.validation.constraints.NotBlank

data class OrderStatusUpdateRequest(
    @field:NotBlank(message = "Статус не может быть пустым")
    val status: String
)