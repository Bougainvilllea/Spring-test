package com.example.shared.exception

sealed class BusinessException(message: String) : RuntimeException(message) {

    // Исключения для User
    class UserNotFound(userId: Long) : BusinessException("User with id $userId not found")
    class UserValidationError(message: String = "Invalid user data") : BusinessException(message)
    class EmailAlreadyExists(email: String) : BusinessException("Email $email already exists")
    class InvalidUserData(message: String) : BusinessException(message)

    // Исключения для Dish
    class DishNotFound(dishId: Long) : BusinessException("Dish with id $dishId not found")
    class DishValidationError(message: String) : BusinessException(message)
    class DishNameAlreadyExists(name: String) : BusinessException("Dish with name '$name' already exists")
    class DishNotAvailable(dishId: Long) : BusinessException("Dish with id $dishId is not available")

    // Исключения для Restaurant
    class RestaurantNotFound(restaurantId: Long) : BusinessException("Restaurant with id $restaurantId not found")
    class RestaurantValidationError(message: String) : BusinessException(message)
    class RestaurantNameAlreadyExists(name: String) : BusinessException("Restaurant with name '$name' already exists")

    // Исключения для Order
    class OrderNotFound(orderId: Long) : BusinessException("Order with id $orderId not found")
    class OrderValidationError(message: String) : BusinessException(message)
    class InvalidOrderStatusTransition(currentStatus: String, newStatus: String) :
        BusinessException("Cannot change order status from $currentStatus to $newStatus")
}