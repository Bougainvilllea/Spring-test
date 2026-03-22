package application.service
import domain.model.Dish
import domain.model.Restaurant
import domain.port.DishRepositoryPort
import domain.port.RestaurantRepositoryPort
import domain.service.RestaurantService
import org.springframework.stereotype.Service
import shared.exception.BusinessException

@Service
class RestaurantService(
    private val restaurantRepositoryPort: RestaurantRepositoryPort,
    private val dishRepositoryPort: DishRepositoryPort
) : RestaurantService {

    override fun getRestaurantById(restaurantId: Long): Restaurant {
        return restaurantRepositoryPort.findById(restaurantId)
            ?: throw BusinessException.RestaurantNotFound(restaurantId)
    }

    override fun getAllRestaurants(): List<Restaurant> {
        return restaurantRepositoryPort.findAll()
    }

    override fun createRestaurant(restaurant: Restaurant): Pair<Restaurant, Boolean> {
        val existingRestaurant = restaurantRepositoryPort.findByName(restaurant.name)

        if (existingRestaurant != null) {
            return Pair(existingRestaurant, false)
        }

        return try {
            Pair(restaurantRepositoryPort.create(restaurant), true)
        } catch (e: IllegalArgumentException) {
            throw BusinessException.RestaurantValidationError(e.message ?: "Invalid restaurant data")
        }
    }

    override fun updateRestaurant(restaurant: Restaurant): Restaurant {
        val existingRestaurant = restaurantRepositoryPort.findById(restaurant.id!!)
            ?: throw BusinessException.RestaurantNotFound(restaurant.id!!)

        val restaurantWithSameName = restaurantRepositoryPort.findByName(restaurant.name)
        if (restaurantWithSameName != null && restaurantWithSameName.id != restaurant.id) {
            throw BusinessException.RestaurantNameAlreadyExists(restaurant.name)
        }

        return restaurantRepositoryPort.update(restaurant)
    }

    override fun deleteRestaurantById(restaurantId: Long): Boolean {
        val restaurantExists = restaurantRepositoryPort.findById(restaurantId)
            ?: throw BusinessException.RestaurantNotFound(restaurantId)

        return restaurantRepositoryPort.deleteById(restaurantId)
    }

    override fun getRestaurantDishes(restaurantId: Long): List<Dish> {
        val restaurantExists = restaurantRepositoryPort.findById(restaurantId)
            ?: throw BusinessException.RestaurantNotFound(restaurantId)

        return dishRepositoryPort.findAllByRestaurantId(restaurantId)
    }
}