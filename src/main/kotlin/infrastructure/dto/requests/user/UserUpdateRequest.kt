package org.example.example.infrastructure.dto.requests.user

data class UserUpdateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
)