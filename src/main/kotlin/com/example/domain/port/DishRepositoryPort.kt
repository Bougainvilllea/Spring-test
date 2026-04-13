package com.example.domain.port
import com.example.domain.model.Dish
import com.example.domain.model.Order
import org.springframework.stereotype.Repository

@Repository
interface DishRepositoryPort : BaseRepositoryPort<Dish> {
    fun findByName(name: String): Dish?
    fun findAllByRestaurantId(restaurantId: Long): List<Dish>
    fun findOrdersContainingDish(dishId: Long): List<Order>
}