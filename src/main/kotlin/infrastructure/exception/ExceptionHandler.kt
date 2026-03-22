package infrastructure.exception

import org.example.example.infrastructure.dto.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.MediaType
import shared.exception.BusinessException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.springframework.validation.FieldError

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun processValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorMessages = ex.bindingResult.allErrors.joinToString("; ") { error ->
            val field = (error as? FieldError)?.field ?: error.objectName
            "$field: ${error.defaultMessage}"
        }

        val response = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = "Request validation failed: $errorMessages"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMalformedRequest(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorText = when (val cause = ex.cause) {
            is InvalidFormatException -> {
                val fieldPath = cause.path.joinToString(".") { it.fieldName }
                "Field '$fieldPath' has invalid format. Expected: ${cause.targetType.simpleName}"
            }
            else -> "Request body is malformed. Check JSON syntax."
        }

        val response = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = errorText
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalidArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid input provided"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(BusinessException.UserNotFound::class)
    fun handleMissingUser(ex: BusinessException.UserNotFound): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "User Not Found",
            message = ex.message ?: "User not found in system"
        )

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(BusinessException.DishNotFound::class)
    fun handleMissingDish(ex: BusinessException.DishNotFound): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Dish Not Found",
            message = ex.message ?: "Dish not found in system"
        )

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(BusinessException.EmailAlreadyExists::class)
    fun handleDuplicateEmail(ex: BusinessException.EmailAlreadyExists): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Email Conflict",
            message = ex.message ?: "Email is already registered"
        )

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(BusinessException.DishNameAlreadyExists::class)
    fun handleDuplicateDishName(ex: BusinessException.DishNameAlreadyExists): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Dish Name Conflict",
            message = ex.message ?: "Dish with this name already exists"
        )

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(
        BusinessException.InvalidUserData::class,
        BusinessException.DishValidationError::class,
        BusinessException.UserValidationError::class
    )
    fun handleDomainValidationError(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = ex.message ?: "Data validation failed"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedError(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        val response = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Error",
            message = "An unexpected error occurred. Contact support."
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }
}