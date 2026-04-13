package com.example.shared.utils.mapper
import com.example.domain.model.Dish
import com.example.infrastructure.dto.requests.dish.DishData
import com.example.infrastructure.dto.response.DishResponse
import com.example.infrastructure.dto.requests.dish.DishCreateRequest
import com.example.infrastructure.dto.requests.dish.DishUpdateRequest
import org.springframework.stereotype.Component
@Component
class DishMapper {

    fun toResponse(dish: Dish): DishResponse {
        return DishResponse(
            id = dish.id,
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurantId = dish.restaurantId
        )
    }

    fun toDomain(dishData: DishData): Dish {
        return Dish(
            id = null,
            name = dishData.name,
            description = dishData.description,
            price = dishData.price,
            isAvailable = dishData.isAvailable,
            restaurantId = dishData.restaurantId
        )
    }

    fun toDomain(createRequest: DishCreateRequest): Dish {
        return Dish(
            id = null,
            name = createRequest.name,
            description = createRequest.description,
            price = createRequest.price,
            isAvailable = createRequest.isAvailable,
            restaurantId = createRequest.restaurantId
        )
    }

    fun toDomain(updateRequest: DishUpdateRequest): Dish {
        return Dish(
            id = null,
            name = updateRequest.name,
            description = updateRequest.description,
            price = updateRequest.price,
            isAvailable = updateRequest.isAvailable,
            restaurantId = updateRequest.restaurantId
        )
    }
}