package infrastructure.dto.response.error
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class ValidationErrorResponse(
      status: Int,
      message: String? = null,
      @JsonProperty("error")
      val errors: Map<String, String>,
      timestamp: LocalDateTime = LocalDateTime.now()
): ErrorResponse(status, message, timestamp=timestamp)