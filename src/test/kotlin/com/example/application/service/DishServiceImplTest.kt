package com.example.application.service

import com.example.domain.model.Dish
import com.example.domain.model.Restaurant
import com.example.domain.port.DishRepositoryPort
import com.example.domain.port.RestaurantRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import com.example.shared.exception.BusinessException
import java.math.BigDecimal
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
@DisplayName("DishService Unit Tests")
class DishServiceImplTest {

    @Mock
    private lateinit var dishRepositoryPort: DishRepositoryPort

    @Mock
    private lateinit var restaurantRepositoryPort: RestaurantRepositoryPort

    @InjectMocks
    private lateinit var dishService: DishServiceImpl

    private val sampleDish = Dish(
        id = 1L,
        name = "Test Dish",
        description = "Delicious test dish",
        price = BigDecimal.valueOf(19.99),
        isAvailable = true,
        restaurantId = 1L
    )

    private val sampleRestaurant = Restaurant(
        id = 1L,
        name = "Test Restaurant",
        address = "Test Address 123"
    )

    @Test
    @DisplayName("Should return dish by id when exists")
    fun findDishById_ValidId_ReturnsDish() {
        `when`(dishRepositoryPort.findById(1L)).thenReturn(sampleDish)

        val outcome = dishService.getDishById(1L)

        assertEquals(sampleDish, outcome)
        verify(dishRepositoryPort).findById(1L)
    }

    @Test
    @DisplayName("Should throw DishNotFound when dish does not exist")
    fun findDishById_InvalidId_ThrowsException() {
        `when`(dishRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.DishNotFound> {
            dishService.getDishById(999L)
        }
        verify(dishRepositoryPort).findById(999L)
    }

    @Test
    @DisplayName("Should return all dishes without filtering")
    fun fetchAllDishes_NoFilter_ReturnsAllItems() {
        val dishes = listOf(sampleDish, sampleDish.copy(id = 2L, name = "Second"))
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val outcome = dishService.getAllDishes(null)

        assertEquals(2, outcome.size)
        verify(dishRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should filter dishes by name part")
    fun fetchAllDishes_WithNameFilter_ReturnsFilteredList() {
        val dishes = listOf(
            sampleDish,
            sampleDish.copy(id = 2L, name = "Pizza"),
            sampleDish.copy(id = 3L, name = "Pasta")
        )
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val outcome = dishService.getAllDishes("pizz")

        assertEquals(1, outcome.size)
        assertEquals("Pizza", outcome[0].name)
        verify(dishRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should return empty list when filter matches no dishes")
    fun fetchAllDishes_NoMatchingFilter_ReturnsEmptyList() {
        val dishes = listOf(sampleDish, sampleDish.copy(id = 2L, name = "Pizza"))
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val outcome = dishService.getAllDishes("xyz")

        assertTrue(outcome.isEmpty())
        verify(dishRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should return existing dish when creating duplicate")
    fun saveOrFetchDish_DuplicateName_ReturnsExisting() {
        val newDish = sampleDish.copy(id = null)
        `when`(dishRepositoryPort.findByName(sampleDish.name)).thenReturn(sampleDish)

        val (outcome, created) = dishService.createOrGetDish(newDish)

        assertFalse(created)
        assertEquals(sampleDish, outcome)
        verify(dishRepositoryPort).findByName(sampleDish.name)
        verify(dishRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new dish when name does not exist")
    fun saveOrFetchDish_NewName_CreatesAndReturns() {
        val newDish = sampleDish.copy(id = null)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.create(newDish)).thenReturn(sampleDish)

        val (outcome, created) = dishService.createOrGetDish(newDish)

        assertTrue(created)
        assertEquals(sampleDish, outcome)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort).create(newDish)
    }

    @Test
    @DisplayName("Should create dish in restaurant successfully")
    fun addDishToEatery_ValidData_CreatesDish() {
        val newDish = sampleDish.copy(id = null, restaurantId = null)

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.create(any())).thenReturn(sampleDish)

        val outcome = dishService.createDishInRestaurant(1L, newDish)

        assertEquals(sampleDish, outcome)
        verify(restaurantRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort).create(any())
    }

    @Test
    @DisplayName("Should throw exception when creating dish in non-existent restaurant")
    fun addDishToEatery_MissingRestaurant_ThrowsError() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            dishService.createDishInRestaurant(999L, sampleDish)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(dishRepositoryPort, never()).findByName(any())
        verify(dishRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should throw exception when creating dish with duplicate name in restaurant")
    fun addDishToEatery_DuplicateName_ThrowsError() {
        val newDish = sampleDish.copy(id = null, restaurantId = null)

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(sampleDish)

        assertThrows<BusinessException.DishNameAlreadyExists> {
            dishService.createDishInRestaurant(1L, newDish)
        }
        verify(restaurantRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should update dish successfully")
    fun modifyDish_ValidData_UpdatesAndReturns() {
        val updatedDish = sampleDish.copy(name = "Updated Dish")

        `when`(dishRepositoryPort.findById(1L)).thenReturn(sampleDish)
        `when`(dishRepositoryPort.findByName(updatedDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.update(updatedDish)).thenReturn(updatedDish)

        val outcome = dishService.updateDish(updatedDish)

        assertEquals("Updated Dish", outcome.name)
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(updatedDish.name)
        verify(dishRepositoryPort).update(updatedDish)
    }

    @Test
    @DisplayName("Should throw DishNotFound when updating non-existent dish")
    fun modifyDish_MissingDish_ThrowsError() {
        val updatedDish = sampleDish.copy(id = 999L)

        `when`(dishRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.DishNotFound> {
            dishService.updateDish(updatedDish)
        }
        verify(dishRepositoryPort).findById(999L)
        verify(dishRepositoryPort, never()).findByName(any())
        verify(dishRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate name")
    fun modifyDish_DuplicateName_ThrowsError() {
        val existingDish = sampleDish.copy(id = 2L, name = "Existing Dish")
        val updatedDish = sampleDish.copy(name = "Existing Dish")

        `when`(dishRepositoryPort.findById(1L)).thenReturn(sampleDish)
        `when`(dishRepositoryPort.findByName("Existing Dish")).thenReturn(existingDish)

        assertThrows<BusinessException.DishNameAlreadyExists> {
            dishService.updateDish(updatedDish)
        }
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName("Existing Dish")
        verify(dishRepositoryPort, never()).update(any())
    }
}