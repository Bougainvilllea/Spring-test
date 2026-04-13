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

    private val testDish = Dish(
        id = 1L,
        name = "Test Dish",
        description = "Delicious test dish",
        price = BigDecimal.valueOf(19.99),
        isAvailable = true,
        restaurantId = 1L
    )

    private val testRestaurant = Restaurant(
        id = 1L,
        name = "Test Restaurant",
        address = "Test Address 123"
    )

    // ==================== getDishById TESTS ====================

    @Test
    @DisplayName("Should return dish by id when exists")
    fun `should return dish by id when exists`() {
        `when`(dishRepositoryPort.findById(1L)).thenReturn(testDish)

        val result = dishService.getDishById(1L)

        assertEquals(testDish, result)
        verify(dishRepositoryPort).findById(1L)
    }

    @Test
    @DisplayName("Should throw DishNotFound when dish does not exist")
    fun `should throw DishNotFound when dish does not exist`() {
        `when`(dishRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.DishNotFound> {
            dishService.getDishById(999L)
        }
        verify(dishRepositoryPort).findById(999L)
    }

    // ==================== getAllDishes TESTS ====================

    @Test
    @DisplayName("Should return all dishes without filtering")
    fun `should return all dishes without filtering`() {
        val dishes = listOf(testDish, testDish.copy(id = 2L, name = "Second"))
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val result = dishService.getAllDishes(null)

        assertEquals(2, result.size)
        verify(dishRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should filter dishes by name part")
    fun `should filter dishes by name part`() {
        val dishes = listOf(
            testDish,
            testDish.copy(id = 2L, name = "Pizza"),
            testDish.copy(id = 3L, name = "Pasta")
        )
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val result = dishService.getAllDishes("pizz")

        assertEquals(1, result.size)
        assertEquals("Pizza", result[0].name)
        verify(dishRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should return empty list when filter matches no dishes")
    fun `should return empty list when filter matches no dishes`() {
        val dishes = listOf(testDish, testDish.copy(id = 2L, name = "Pizza"))
        `when`(dishRepositoryPort.findAll()).thenReturn(dishes)

        val result = dishService.getAllDishes("xyz")

        assertTrue(result.isEmpty())
        verify(dishRepositoryPort).findAll()
    }

    // ==================== createOrGetDish TESTS ====================

    @Test
    @DisplayName("Should return existing dish when creating duplicate")
    fun `should return existing dish when creating duplicate`() {
        val newDish = testDish.copy(id = null)
        `when`(dishRepositoryPort.findByName(testDish.name)).thenReturn(testDish)

        val (result, wasCreated) = dishService.createOrGetDish(newDish)

        assertFalse(wasCreated)
        assertEquals(testDish, result)
        verify(dishRepositoryPort).findByName(testDish.name)
        verify(dishRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new dish when name does not exist")
    fun `should create new dish when name does not exist`() {
        val newDish = testDish.copy(id = null)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.create(newDish)).thenReturn(testDish)

        val (result, wasCreated) = dishService.createOrGetDish(newDish)

        assertTrue(wasCreated)
        assertEquals(testDish, result)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort).create(newDish)
    }

    // ==================== createDishInRestaurant TESTS ====================

    @Test
    @DisplayName("Should create dish in restaurant successfully")
    fun `should create dish in restaurant successfully`() {
        val newDish = testDish.copy(id = null, restaurantId = null)

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.create(any())).thenReturn(testDish)

        val result = dishService.createDishInRestaurant(1L, newDish)

        assertEquals(testDish, result)
        verify(restaurantRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort).create(any())
    }

    @Test
    @DisplayName("Should throw exception when creating dish in non-existent restaurant")
    fun `should throw exception when creating dish in non-existent restaurant`() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            dishService.createDishInRestaurant(999L, testDish)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(dishRepositoryPort, never()).findByName(any())
        verify(dishRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should throw exception when creating dish with duplicate name in restaurant")
    fun `should throw exception when creating dish with duplicate name in restaurant`() {
        val newDish = testDish.copy(id = null, restaurantId = null)

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)
        `when`(dishRepositoryPort.findByName(newDish.name)).thenReturn(testDish)

        assertThrows<BusinessException.DishNameAlreadyExists> {
            dishService.createDishInRestaurant(1L, newDish)
        }
        verify(restaurantRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(newDish.name)
        verify(dishRepositoryPort, never()).create(any())
    }

    // ==================== updateDish TESTS ====================

    @Test
    @DisplayName("Should update dish successfully")
    fun `should update dish successfully`() {
        val updatedDish = testDish.copy(name = "Updated Dish")

        `when`(dishRepositoryPort.findById(1L)).thenReturn(testDish)
        `when`(dishRepositoryPort.findByName(updatedDish.name)).thenReturn(null)
        `when`(dishRepositoryPort.update(updatedDish)).thenReturn(updatedDish)

        val result = dishService.updateDish(updatedDish)

        assertEquals("Updated Dish", result.name)
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName(updatedDish.name)
        verify(dishRepositoryPort).update(updatedDish)
    }

    @Test
    @DisplayName("Should throw DishNotFound when updating non-existent dish")
    fun `should throw DishNotFound when updating non-existent dish`() {
        val updatedDish = testDish.copy(id = 999L)

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
    fun `should throw exception when updating with duplicate name`() {
        val existingDish = testDish.copy(id = 2L, name = "Existing Dish")
        val updatedDish = testDish.copy(name = "Existing Dish")

        `when`(dishRepositoryPort.findById(1L)).thenReturn(testDish)
        `when`(dishRepositoryPort.findByName("Existing Dish")).thenReturn(existingDish)

        assertThrows<BusinessException.DishNameAlreadyExists> {
            dishService.updateDish(updatedDish)
        }
        verify(dishRepositoryPort).findById(1L)
        verify(dishRepositoryPort).findByName("Existing Dish")
        verify(dishRepositoryPort, never()).update(any())
    }
}