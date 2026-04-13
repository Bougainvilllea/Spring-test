package com.example.domain.port
import com.example.domain.model.Restaurant
import org.springframework.stereotype.Component

@Component
interface RestaurantRepositoryPort : BaseRepositoryPort<Restaurant> {
    fun findByName(name: String): Restaurant?
}