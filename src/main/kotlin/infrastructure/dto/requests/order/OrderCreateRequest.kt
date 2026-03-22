package org.example.example.infrastructure.dto.requests.order

data class OrderCreateRequest(
    val userId: Long,
    val dishIds: List<Long>
)