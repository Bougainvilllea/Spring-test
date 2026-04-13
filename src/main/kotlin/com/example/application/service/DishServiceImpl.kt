package com.example.application.service

import com.example.domain.model.Dish
import com.example.domain.port.DishRepositoryPort
import com.example.domain.service.DishService
import org.springframework.stereotype.Service
import com.example.shared.exception.BusinessException
import com.example.domain.port.RestaurantRepositoryPort
import jakarta.transaction.Transactional

@Service
class DishServiceImpl(
    private val dishRepositoryPort: DishRepositoryPort,
    private val restaurantRepositoryPort: RestaurantRepositoryPort
) : DishService {

    override fun getDishById(dishId: Long): Dish {
        return dishRepositoryPort.findById(dishId)
            ?: throw BusinessException.DishNotFound(dishId)
    }

    override fun getAllDishes(namePart: String?): List<Dish> {
        val allDishes = dishRepositoryPort.findAll()
        return if (namePart.isNullOrBlank()) {
            allDishes
        } else {
            allDishes.filter { it.name.contains(namePart, ignoreCase = true) }
        }
    }

    override fun createOrGetDish(dish: Dish): Pair<Dish, Boolean> {
        val existingDish = dishRepositoryPort.findByName(dish.name)

        if (existingDish != null) {
            return Pair(existingDish, false)
        }

        return try {
            Pair(dishRepositoryPort.create(dish), true)
        } catch (e: IllegalArgumentException) {
            throw BusinessException.DishValidationError(e.message ?: "Invalid dish data")
        }
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

    override fun updateDish(dish: Dish): Dish {
        val existingDish = dishRepositoryPort.findById(dish.id!!)
            ?: throw BusinessException.DishNotFound(dish.id!!)

        val dishWithSameName = dishRepositoryPort.findByName(dish.name)
        if (dishWithSameName != null && dishWithSameName.id != dish.id) {
            throw BusinessException.DishNameAlreadyExists(dish.name)
        }

        return dishRepositoryPort.update(dish)
    }

    @Transactional
    override fun deleteDishById(dishId: Long): Boolean {
        val dishExists = dishRepositoryPort.findById(dishId)
            ?: throw BusinessException.DishNotFound(dishId)

        val ordersWithDish = dishRepositoryPort.findOrdersContainingDish(dishId)

        return dishRepositoryPort.deleteById(dishId)
    }
}