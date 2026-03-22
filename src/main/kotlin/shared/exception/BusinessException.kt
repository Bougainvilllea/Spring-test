package shared.exception

sealed class BusinessException(message: String) : RuntimeException(message) {
    class UserNotFound(userId: Long) : BusinessException("User $userId does not exist")
    class UserValidationError(message: String = "User data validation failed") : BusinessException(message)
    class EmailAlreadyExists(email: String) : BusinessException("The email $email is already registered")
    class InvalidUserData(message: String) : BusinessException(message)
    class DishNotFound(dishId: Long) : BusinessException("Dish $dishId does not exist")
    class DishValidationError(message: String) : BusinessException(message)
    class DishNameAlreadyExists(name: String) : BusinessException("A dish named '$name' already exists")
    class RestaurantNotFound(restaurantId: Long) : BusinessException("Restaurant $restaurantId does not exist")
    class RestaurantValidationError(message: String) : BusinessException(message)
    class RestaurantNameAlreadyExists(name: String) : BusinessException("A restaurant named '$name' already exists")
    class OrderNotFound(orderId: Long) : BusinessException("Order $orderId does not exist")
    class OrderValidationError(message: String) : BusinessException(message)
    class InvalidOrderStatusTransition(currentStatus: String, newStatus: String) :
        BusinessException("Unable to change order status from $currentStatus to $newStatus")
}