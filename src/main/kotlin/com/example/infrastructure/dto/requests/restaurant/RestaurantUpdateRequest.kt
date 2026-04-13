package com.example.infrastructure.dto.requests.restaurant

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RestaurantUpdateRequest(
    @field:NotBlank(message = "Название не может быть пустым")
    @field:Size(min = 2, max = 100, message = "Название: от 2 до 100 символов")
    val name: String,

    @field:NotBlank(message = "Адрес не может быть пустым")
    val address: String
)