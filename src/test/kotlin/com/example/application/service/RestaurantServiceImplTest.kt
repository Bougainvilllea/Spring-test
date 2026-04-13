package com.example.application.service

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
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
@DisplayName("RestaurantService Unit Tests")
class RestaurantServiceImplTest {

    @Mock
    private lateinit var restaurantRepositoryPort: RestaurantRepositoryPort

    @Mock
    private lateinit var dishRepository: DishRepositoryPort

    @InjectMocks
    private lateinit var restaurantService: RestaurantServiceImpl

    private val testRestaurant = Restaurant(
        id = 1L,
        name = "Test Restaurant",
        address = "Test Address 123"
    )

    // ==================== getRestaurantById TESTS ====================

    @Test
    @DisplayName("Should return restaurant by id when exists")
    fun `should return restaurant by id when exists`() {
        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)

        val result = restaurantService.getRestaurantById(1L)

        assertEquals(testRestaurant, result)
        verify(restaurantRepositoryPort).findById(1L)
    }

    @Test
    @DisplayName("Should throw RestaurantNotFound when restaurant does not exist")
    fun `should throw RestaurantNotFound when restaurant does not exist`() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.getRestaurantById(999L)
        }

        assertEquals("Restaurant with id 999 not found", exception.message)
        verify(restaurantRepositoryPort).findById(999L)
    }

    // ==================== getAllRestaurants TESTS ====================

    @Test
    @DisplayName("Should return all restaurants")
    fun `should return all restaurants`() {
        val restaurants = listOf(testRestaurant, testRestaurant.copy(id = 2L, name = "Second"))
        `when`(restaurantRepositoryPort.findAll()).thenReturn(restaurants)

        val result = restaurantService.getAllRestaurants()

        assertEquals(2, result.size)
        assertEquals(restaurants, result)
        verify(restaurantRepositoryPort).findAll()
    }

    // ==================== createRestaurant TESTS ====================

    @Test
    @DisplayName("Should return existing restaurant when creating duplicate")
    fun `should return existing restaurant when creating duplicate`() {
        `when`(restaurantRepositoryPort.findByName(testRestaurant.name)).thenReturn(testRestaurant)

        val (result, wasCreated) = restaurantService.createRestaurant(testRestaurant)

        assertFalse(wasCreated)
        assertEquals(testRestaurant, result)
        verify(restaurantRepositoryPort).findByName(testRestaurant.name)
        verify(restaurantRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new restaurant when name does not exist")
    fun `should create new restaurant when name does not exist`() {
        val newRestaurant = testRestaurant.copy(id = null)
        val createdRestaurant = testRestaurant

        `when`(restaurantRepositoryPort.findByName(newRestaurant.name)).thenReturn(null)
        `when`(restaurantRepositoryPort.create(newRestaurant)).thenReturn(createdRestaurant)

        val (result, wasCreated) = restaurantService.createRestaurant(newRestaurant)

        assertTrue(wasCreated)
        assertEquals(createdRestaurant, result)
        verify(restaurantRepositoryPort).findByName(newRestaurant.name)
        verify(restaurantRepositoryPort).create(newRestaurant)
    }

    // ==================== updateRestaurant TESTS ====================

    @Test
    @DisplayName("Should update restaurant successfully")
    fun `should update restaurant successfully`() {
        val updatedRestaurant = testRestaurant.copy(name = "Updated Name")

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)
        `when`(restaurantRepositoryPort.findByName(updatedRestaurant.name)).thenReturn(null)
        `when`(restaurantRepositoryPort.update(updatedRestaurant)).thenReturn(updatedRestaurant)

        val result = restaurantService.updateRestaurant(updatedRestaurant)

        assertEquals("Updated Name", result.name)
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).findByName(updatedRestaurant.name)
        verify(restaurantRepositoryPort).update(updatedRestaurant)
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent restaurant")
    fun `should throw exception when updating non-existent restaurant`() {
        val updatedRestaurant = testRestaurant.copy(id = 999L)

        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.updateRestaurant(updatedRestaurant)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(restaurantRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate name")
    fun `should throw exception when updating with duplicate name`() {
        val existingRestaurant = Restaurant(id = 2L, name = "Existing", address = "Address")
        val updatedRestaurant = testRestaurant.copy(name = "Existing")

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)
        `when`(restaurantRepositoryPort.findByName("Existing")).thenReturn(existingRestaurant)

        assertThrows<BusinessException.RestaurantNameAlreadyExists> {
            restaurantService.updateRestaurant(updatedRestaurant)
        }
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).findByName("Existing")
        verify(restaurantRepositoryPort, never()).update(any())
    }

    // ==================== deleteRestaurantById TESTS ====================

    @Test
    @DisplayName("Should delete restaurant successfully")
    fun `should delete restaurant successfully`() {
        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(testRestaurant)
        `when`(restaurantRepositoryPort.deleteById(1L)).thenReturn(true)

        val result = restaurantService.deleteRestaurantById(1L)

        assertTrue(result)
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).deleteById(1L)
    }

    @Test
    @DisplayName("Should throw RestaurantNotFound when deleting non-existent restaurant")
    fun `should throw RestaurantNotFound when deleting non-existent restaurant`() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.deleteRestaurantById(999L)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(restaurantRepositoryPort, never()).deleteById(any())
    }
}