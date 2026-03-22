package infrastructure.web.controller

import domain.model.Dish
import domain.service.DishService
import infrastructure.dto.requests.dish.DishCreateRequest
import infrastructure.dto.requests.dish.DishUpdateRequest
import infrastructure.dto.response.DishResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shared.mapper.DishMapper

@RestController
@RequestMapping("/api/v1/dishes")
class DishController(
    private val dishService: DishService,
    private val dishMapper: DishMapper
) {

    @GetMapping
    fun listDishes(@RequestParam(required = false) namePart: String?): ResponseEntity<List<DishResponse>> {
        val dishesDomain: List<Dish> = dishService.getAllDishes(namePart)
        val response: List<DishResponse> = dishesDomain.map { dishMapper.toResponse(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createDish(@RequestBody createRequest: DishCreateRequest): ResponseEntity<DishResponse> {
        val dishFromRequest = dishMapper.toDomain(createRequest)
        val (resultDish, wasCreated) = dishService.createOrGetDish(dishFromRequest)
        val response = dishMapper.toResponse(resultDish)

        val status = if (wasCreated) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/{id}")
    fun getDishById(@PathVariable id: Long): ResponseEntity<DishResponse> {
        val dish = dishService.getDishById(id)
        val response = dishMapper.toResponse(dish)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateDish(@PathVariable id: Long, @RequestBody updateRequest: DishUpdateRequest): ResponseEntity<DishResponse> {
        val dish = dishMapper.toDomain(updateRequest).copy(id = id)
        val updatedDish = dishService.updateDish(dish)
        val response = dishMapper.toResponse(updatedDish)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteDish(@PathVariable id: Long): ResponseEntity<Unit> {
        dishService.deleteDishById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}