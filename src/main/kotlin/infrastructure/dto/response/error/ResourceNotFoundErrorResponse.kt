package infrastructure.dto.response.error

import java.time.LocalDateTime

class ResourceNotFoundErrorResponse(
    status: Int,
    error: String,
    message: String? = null,
    val resourceType: String,
    val resourceId: Any? = null,
    timestamp: LocalDateTime = LocalDateTime.now()
) : ErrorResponse(status, message, error, timestamp)