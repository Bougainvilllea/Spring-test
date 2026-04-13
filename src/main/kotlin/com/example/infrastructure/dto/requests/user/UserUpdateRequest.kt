package com.example.infrastructure.dto.requests.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String,

    @field:NotBlank(message = "Имя не может быть пустым")
    @field:Size(min = 2, max = 50, message = "Имя: от 2 до 50 символов")
    val firstName: String,

    @field:NotBlank(message = "Фамилия не может быть пустой")
    @field:Size(min = 2, max = 50, message = "Фамилия: от 2 до 50 символов")
    val lastName: String,

    val isActive: Boolean = true
)