package com.example.domain.service
import com.example.domain.model.Dish

interface DishService {
    fun getDishById(dishId: Long): Dish
    fun getAllDishes(namePart: String?): List<Dish>
    fun createOrGetDish(dish: Dish): Pair<Dish, Boolean>
    fun createDishInRestaurant(restaurantId: Long, dish: Dish): Dish  // Новый метод
    fun updateDish(dish: Dish): Dish
    fun deleteDishById(dishId: Long): Boolean
}