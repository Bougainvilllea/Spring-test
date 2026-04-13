package com.example.domain.service
import com.example.domain.model.Dish
import com.example.domain.model.Restaurant


interface RestaurantService {
    fun getRestaurantById(restaurantId: Long): Restaurant
    fun getAllRestaurants(): List<Restaurant>
    fun createRestaurant(restaurant: Restaurant): Pair<Restaurant, Boolean>
    fun updateRestaurant(restaurant: Restaurant): Restaurant
    fun deleteRestaurantById(restaurantId: Long): Boolean
    fun getRestaurantDishes(restaurantId: Long): List<Dish>
}