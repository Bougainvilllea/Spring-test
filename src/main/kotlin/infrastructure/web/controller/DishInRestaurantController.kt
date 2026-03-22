package infrastructure.web.controller

import domain.service.DishService
import infrastructure.dto.requests.dish.DishCreateRequest
import infrastructure.dto.response.DishResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shared.mapper.DishMapper

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/dishes")
class DishInRestaurantController(
    private val dishService: DishService,
    private val dishMapper: DishMapper
) {

    @PostMapping
    fun createDishInRestaurant(
        @PathVariable restaurantId: Long,
        @RequestBody createRequest: DishCreateRequest
    ): ResponseEntity<DishResponse> {
        val dish = dishMapper.toDomain(createRequest)
        val createdDish = dishService.createDishInRestaurant(restaurantId, dish)
        val response = dishMapper.toResponse(createdDish)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}