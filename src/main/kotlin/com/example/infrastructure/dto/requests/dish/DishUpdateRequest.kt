package com.example.infrastructure.dto.requests.dish

import java.math.BigDecimal
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class DishUpdateRequest(
    @field:NotBlank(message = "Название не может быть пустым")
    val name: String,

    val description: String? = null,

    @field:NotNull(message = "Цена обязательна")
    @field:Min(value = 1, message = "Цена должна быть больше 0")
    val price: BigDecimal,

    @field:NotNull(message = "Статус доступности обязателен")
    val isAvailable: Boolean,

    @field:NotNull(message = "ID ресторана обязателен")
    val restaurantId: Long
)