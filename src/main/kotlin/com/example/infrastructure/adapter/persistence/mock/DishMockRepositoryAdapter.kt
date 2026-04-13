package com.example.infrastructure.adapter.persistence.mock
import com.example.domain.model.Dish
import com.example.domain.port.DishRepositoryPort
import com.example.domain.model.Order
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("mock")
class DishMockRepositoryAdapter : BaseMockRepositoryAdapter<Dish>(), DishRepositoryPort {
    override fun findByName(name: String): Dish? {
        return storage.values.find { it.name.equals(name, ignoreCase = true) }
    }

    override fun findAllByRestaurantId(restaurantId: Long): List<Dish> {
        TODO("Not yet implemented")
    }

    override fun findOrdersContainingDish(dishId: Long): List<Order> {
        TODO("Not yet implemented")
    }
}