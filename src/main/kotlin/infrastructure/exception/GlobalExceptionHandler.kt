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
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors.joinToString("; ") { error ->
            val fieldName = (error as? FieldError)?.field ?: error.objectName
            "$fieldName: ${error.defaultMessage}"
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = "Invalid request body: $errors"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val message = when (val cause = ex.cause) {
            is InvalidFormatException -> {
                val fieldName = cause.path.joinToString(".") { it.fieldName }
                "Invalid value for field '$fieldName'. Expected type: ${cause.targetType.simpleName}"
            }
            else -> "Malformed JSON request body. Please check your request format."
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = message
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid input data"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка User исключений
    @ExceptionHandler(BusinessException.UserNotFound::class)
    fun handleUserNotFound(ex: BusinessException.UserNotFound): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "User Not Found",
            message = ex.message ?: "User not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка Dish исключений
    @ExceptionHandler(BusinessException.DishNotFound::class)
    fun handleDishNotFound(ex: BusinessException.DishNotFound): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Dish Not Found",
            message = ex.message ?: "Dish not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    @ExceptionHandler(BusinessException.DishNameAlreadyExists::class)
    fun handleDishNameExists(ex: BusinessException.DishNameAlreadyExists): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Dish Name Already Exists",
            message = ex.message ?: "Dish name already exists"
        )
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка Restaurant исключений
    @ExceptionHandler(BusinessException.RestaurantNotFound::class)
    fun handleRestaurantNotFound(ex: BusinessException.RestaurantNotFound): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Restaurant Not Found",
            message = ex.message ?: "Restaurant not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    @ExceptionHandler(BusinessException.RestaurantNameAlreadyExists::class)
    fun handleRestaurantNameExists(ex: BusinessException.RestaurantNameAlreadyExists): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Restaurant Name Already Exists",
            message = ex.message ?: "Restaurant name already exists"
        )
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка Order исключений
    @ExceptionHandler(BusinessException.OrderNotFound::class)
    fun handleOrderNotFound(ex: BusinessException.OrderNotFound): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Order Not Found",
            message = ex.message ?: "Order not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    @ExceptionHandler(BusinessException.InvalidOrderStatusTransition::class)
    fun handleInvalidOrderStatusTransition(ex: BusinessException.InvalidOrderStatusTransition): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Invalid Status Transition",
            message = ex.message ?: "Invalid order status transition"
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка OrderValidationError - возвращаем 400 BAD REQUEST
    @ExceptionHandler(BusinessException.OrderValidationError::class)
    fun handleOrderValidationError(ex: BusinessException.OrderValidationError): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = ex.message ?: "Invalid order data"
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка общих валидационных исключений
    @ExceptionHandler(
        BusinessException.InvalidUserData::class,
        BusinessException.DishValidationError::class,
        BusinessException.UserValidationError::class,
        BusinessException.RestaurantValidationError::class
    )
    fun handleBusinessValidationError(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = ex.message ?: "Invalid data provided"
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Обработка Email конфликта
    @ExceptionHandler(BusinessException.EmailAlreadyExists::class)
    fun handleEmailExists(ex: BusinessException.EmailAlreadyExists): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Email Already Exists",
            message = ex.message ?: "Email already exists"
        )
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }

    // Общая обработка исключений
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred. Please try again later."
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse)
    }
}