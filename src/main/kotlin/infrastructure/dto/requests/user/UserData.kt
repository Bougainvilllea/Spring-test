package org.example.example.infrastructure.dto.requests.user

data class UserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
)