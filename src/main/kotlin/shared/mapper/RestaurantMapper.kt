package shared.mapper

import domain.model.Restaurant
import infrastructure.dto.requests.restaurant.RestaurantCreateRequest
import org.example.example.infrastructure.dto.requests.restaurant.RestaurantUpdateRequest
import org.example.example.infrastructure.dto.response.restaurant.RestaurantResponse
import org.springframework.stereotype.Component

@Component
class RestaurantMapper {

    fun toResponse(restaurant: Restaurant): RestaurantResponse {
        return RestaurantResponse(
            id = restaurant.id,
            name = restaurant.name,
            address = restaurant.address
        )
    }

    fun toDomain(createRequest: RestaurantCreateRequest): Restaurant {
        return Restaurant(
            id = null,
            name = createRequest.name,
            address = createRequest.address
        )
    }

    fun toDomain(updateRequest: RestaurantUpdateRequest): Restaurant {
        return Restaurant(
            id = null,
            name = updateRequest.name,
            address = updateRequest.address
        )
    }
}