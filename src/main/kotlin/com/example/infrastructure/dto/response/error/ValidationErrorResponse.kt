package com.example.infrastructure.dto.response.error
import com.fasterxml.jackson.annotation.JsonProperty
import com.example.infrastructure.dto.response.error.common.ErrorResponse
import java.time.LocalDateTime

class ValidationErrorResponse(
      status: Int,
      message: String? = null,
      @JsonProperty("error")
      val errors: Map<String, String>,
      timestamp: LocalDateTime = LocalDateTime.now()
): ErrorResponse(status, message, timestamp=timestamp)