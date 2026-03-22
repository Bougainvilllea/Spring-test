package domain.service
import domain.model.Dish
import domain.model.Restaurant


interface RestaurantService {
    fun getRestaurantById(restaurantId: Long): Restaurant
    fun getAllRestaurants(): List<Restaurant>
    fun createRestaurant(restaurant: Restaurant): Pair<Restaurant, Boolean>
    fun updateRestaurant(restaurant: Restaurant): Restaurant
    fun deleteRestaurantById(restaurantId: Long): Boolean
    fun getRestaurantDishes(restaurantId: Long): List<Dish>
}