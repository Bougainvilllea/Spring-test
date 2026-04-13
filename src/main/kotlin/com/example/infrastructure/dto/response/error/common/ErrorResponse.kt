package com.example.infrastructure.dto.response.error.common

import java.time.LocalDateTime

open class ErrorResponse(
    val status: Int,
    val message: String? = null,
    val error: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)