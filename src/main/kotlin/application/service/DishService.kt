package application.service

import domain.model.Dish
import domain.port.DishRepositoryPort
import domain.service.DishService
import org.springframework.stereotype.Service
import shared.exception.BusinessException
import domain.port.RestaurantRepositoryPort
import jakarta.transaction.Transactional

@Service
class DishServiceImpl(
    private val dishRepositoryPort: DishRepositoryPort,
    private val restaurantRepositoryPort: RestaurantRepositoryPort
) : DishService {

    override fun getDishById(dishIdentifier: Long): Dish {
        return dishRepositoryPort.findById(dishIdentifier)
            ?: throw BusinessException.DishNotFound(dishIdentifier)
    }

    override fun getAllDishes(nameFilter: String?): List<Dish> {
        val allDishes = dishRepositoryPort.findAll()
        return if (nameFilter.isNullOrBlank()) {
            allDishes
        } else {
            allDishes.filter { it.name.contains(nameFilter, ignoreCase = true) }
        }
    }

    override fun createOrGetDish(dishToProcess: Dish): Pair<Dish, Boolean> {
        val existingDish = dishRepositoryPort.findByName(dishToProcess.name)

        if (existingDish != null) {
            return Pair(existingDish, false)
        }

        return try {
            Pair(dishRepositoryPort.create(dishToProcess), true)
        } catch (e: IllegalArgumentException) {
            throw BusinessException.DishValidationError(e.message ?: "Invalid dish data")
        }
    }

    override fun findByName(dishName: String): Dish? {
        TODO("Not yet implemented")
    }

    override fun createDishInRestaurant(restaurantId: Long, dish: Dish): Dish {
        val restaurant = restaurantRepositoryPort.findById(restaurantId)
            ?: throw BusinessException.RestaurantNotFound(restaurantId)

        val existingDish = dishRepositoryPort.findByName(dish.name)
        if (existingDish != null) {
            throw BusinessException.DishNameAlreadyExists(dish.name)
        }

        val dishWithRestaurant = dish.copy(restaurantId = restaurantId)

        return try {
            dishRepositoryPort.create(dishWithRestaurant)
        } catch (e: IllegalArgumentException) {
            throw BusinessException.DishValidationError(e.message ?: "Invalid dish data")
        }
    }

    override fun updateDish(dishToModify: Dish): Dish {
        val existingDish = dishRepositoryPort.findById(dishToModify.id!!)
            ?: throw BusinessException.DishNotFound(dishToModify.id!!)

        val dishWithSameName = dishRepositoryPort.findByName(dishToModify.name)
        if (dishWithSameName != null && dishWithSameName.id != dishToModify.id) {
            throw BusinessException.DishNameAlreadyExists(dishToModify.name)
        }

        return dishRepositoryPort.update(dishToModify)
    }

    @Transactional
    override fun deleteDishById(dishIdentifier: Long): Boolean {
        val dishExists = dishRepositoryPort.findById(dishIdentifier)
            ?: throw BusinessException.DishNotFound(dishIdentifier)

        val ordersWithDish = dishRepositoryPort.findOrdersContainingDish(dishIdentifier)

        return dishRepositoryPort.deleteById(dishIdentifier)
    }
}