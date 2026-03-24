package infrastructure.dto.response.error

import java.time.LocalDateTime

class ConflictErrorResponse(
    status: Int,
    error: String,
    message: String? = null,
    val resourceType: String? = null,
    val resourceIdentifier: String? = null,
    timestamp: LocalDateTime = LocalDateTime.now()
) : ErrorResponse(status, message, error, timestamp)