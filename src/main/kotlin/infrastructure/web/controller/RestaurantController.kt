package infrastructure.web.controller

import infrastructure.dto.requests.restaurant.RestaurantCreateRequest
import org.example.example.infrastructure.dto.requests.restaurant.RestaurantUpdateRequest
import infrastructure.dto.response.DishResponse
import org.example.example.infrastructure.dto.response.restaurant.RestaurantResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shared.mapper.DishMapper
import shared.mapper.RestaurantMapper
import domain.service.RestaurantService

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantController(
    private val restaurantService: RestaurantService,
    private val restaurantMapper: RestaurantMapper,
    private val dishMapper: DishMapper
) {

    @GetMapping
    fun listRestaurants(): ResponseEntity<List<RestaurantResponse>> {
        val restaurants = restaurantService.getAllRestaurants()
        val response = restaurants.map { restaurantMapper.toResponse(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createRestaurant(@RequestBody createRequest: RestaurantCreateRequest): ResponseEntity<RestaurantResponse> {
        val restaurant = restaurantMapper.toDomain(createRequest)
        val (resultRestaurant, wasCreated) = restaurantService.createRestaurant(restaurant)
        val response = restaurantMapper.toResponse(resultRestaurant)

        val status = if (wasCreated) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/{id}")
    fun getRestaurantById(@PathVariable id: Long): ResponseEntity<RestaurantResponse> {
        val restaurant = restaurantService.getRestaurantById(id)
        val response = restaurantMapper.toResponse(restaurant)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateRestaurant(
        @PathVariable id: Long,
        @RequestBody updateRequest: RestaurantUpdateRequest
    ): ResponseEntity<RestaurantResponse> {
        val restaurant = restaurantMapper.toDomain(updateRequest).copy(id = id)
        val updatedRestaurant = restaurantService.updateRestaurant(restaurant)
        val response = restaurantMapper.toResponse(updatedRestaurant)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteRestaurant(@PathVariable id: Long): ResponseEntity<Unit> {
        restaurantService.deleteRestaurantById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @GetMapping("/{id}/dishes")
    fun getRestaurantDishes(@PathVariable id: Long): ResponseEntity<List<DishResponse>> {
        val dishes = restaurantService.getRestaurantDishes(id)
        val response = dishes.map { dishMapper.toResponse(it) }
        return ResponseEntity.ok(response)
    }
}