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

    private val sampleRestaurant = Restaurant(
        id = 1L,
        name = "Test Restaurant",
        address = "Test Address 123"
    )

    @Test
    @DisplayName("Should return restaurant by id when exists")
    fun fetchEateryById_ValidId_ReturnsEatery() {
        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)

        val outcome = restaurantService.getRestaurantById(1L)

        assertEquals(sampleRestaurant, outcome)
        verify(restaurantRepositoryPort).findById(1L)
    }

    @Test
    @DisplayName("Should throw RestaurantNotFound when restaurant does not exist")
    fun fetchEateryById_InvalidId_ThrowsError() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        val exception = assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.getRestaurantById(999L)
        }

        assertEquals("Restaurant with id 999 not found", exception.message)
        verify(restaurantRepositoryPort).findById(999L)
    }

    @Test
    @DisplayName("Should return all restaurants")
    fun fetchAllEateries_ReturnsAllItems() {
        val eateries = listOf(sampleRestaurant, sampleRestaurant.copy(id = 2L, name = "Second"))
        `when`(restaurantRepositoryPort.findAll()).thenReturn(eateries)

        val outcome = restaurantService.getAllRestaurants()

        assertEquals(2, outcome.size)
        assertEquals(eateries, outcome)
        verify(restaurantRepositoryPort).findAll()
    }

    @Test
    @DisplayName("Should return existing restaurant when creating duplicate")
    fun saveOrFetchEatery_DuplicateName_ReturnsExisting() {
        `when`(restaurantRepositoryPort.findByName(sampleRestaurant.name)).thenReturn(sampleRestaurant)

        val (outcome, created) = restaurantService.createRestaurant(sampleRestaurant)

        assertFalse(created)
        assertEquals(sampleRestaurant, outcome)
        verify(restaurantRepositoryPort).findByName(sampleRestaurant.name)
        verify(restaurantRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new restaurant when name does not exist")
    fun saveOrFetchEatery_NewName_CreatesAndReturns() {
        val newEatery = sampleRestaurant.copy(id = null)
        val createdEatery = sampleRestaurant

        `when`(restaurantRepositoryPort.findByName(newEatery.name)).thenReturn(null)
        `when`(restaurantRepositoryPort.create(newEatery)).thenReturn(createdEatery)

        val (outcome, created) = restaurantService.createRestaurant(newEatery)

        assertTrue(created)
        assertEquals(createdEatery, outcome)
        verify(restaurantRepositoryPort).findByName(newEatery.name)
        verify(restaurantRepositoryPort).create(newEatery)
    }

    @Test
    @DisplayName("Should update restaurant successfully")
    fun modifyEatery_ValidData_UpdatesAndReturns() {
        val updatedEatery = sampleRestaurant.copy(name = "Updated Name")

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)
        `when`(restaurantRepositoryPort.findByName(updatedEatery.name)).thenReturn(null)
        `when`(restaurantRepositoryPort.update(updatedEatery)).thenReturn(updatedEatery)

        val outcome = restaurantService.updateRestaurant(updatedEatery)

        assertEquals("Updated Name", outcome.name)
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).findByName(updatedEatery.name)
        verify(restaurantRepositoryPort).update(updatedEatery)
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent restaurant")
    fun modifyEatery_MissingEatery_ThrowsError() {
        val updatedEatery = sampleRestaurant.copy(id = 999L)

        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.updateRestaurant(updatedEatery)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(restaurantRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate name")
    fun modifyEatery_DuplicateName_ThrowsError() {
        val existingEatery = Restaurant(id = 2L, name = "Existing", address = "Address")
        val updatedEatery = sampleRestaurant.copy(name = "Existing")

        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)
        `when`(restaurantRepositoryPort.findByName("Existing")).thenReturn(existingEatery)

        assertThrows<BusinessException.RestaurantNameAlreadyExists> {
            restaurantService.updateRestaurant(updatedEatery)
        }
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).findByName("Existing")
        verify(restaurantRepositoryPort, never()).update(any())
    }

    @Test
    @DisplayName("Should delete restaurant successfully")
    fun removeEateryById_ValidId_DeletesAndReturnsTrue() {
        `when`(restaurantRepositoryPort.findById(1L)).thenReturn(sampleRestaurant)
        `when`(restaurantRepositoryPort.deleteById(1L)).thenReturn(true)

        val outcome = restaurantService.deleteRestaurantById(1L)

        assertTrue(outcome)
        verify(restaurantRepositoryPort).findById(1L)
        verify(restaurantRepositoryPort).deleteById(1L)
    }

    @Test
    @DisplayName("Should throw RestaurantNotFound when deleting non-existent restaurant")
    fun removeEateryById_InvalidId_ThrowsError() {
        `when`(restaurantRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.RestaurantNotFound> {
            restaurantService.deleteRestaurantById(999L)
        }
        verify(restaurantRepositoryPort).findById(999L)
        verify(restaurantRepositoryPort, never()).deleteById(any())
    }
}