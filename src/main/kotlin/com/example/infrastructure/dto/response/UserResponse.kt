package com.example.infrastructure.dto.response

data class UserResponse(
    val id: Long?,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
)
