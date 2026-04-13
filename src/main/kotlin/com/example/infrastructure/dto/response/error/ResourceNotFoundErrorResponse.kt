package com.example.infrastructure.dto.response.error

import com.example.infrastructure.dto.response.error.common.ErrorResponse
import java.time.LocalDateTime

class ResourceNotFoundErrorResponse(
    status: Int,
    error: String,
    message: String? = null,
    val resourceType: String,
    val resourceId: Any? = null,
    timestamp: LocalDateTime = LocalDateTime.now()
) : ErrorResponse(status, message, error, timestamp)