package org.example.example.infrastructure.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty("status")
    val status: Int,

    @JsonProperty("error")
    val error: String,

    @JsonProperty("message")
    val message: String
)