package domain.service
import domain.model.Dish

interface DishService {
    fun getDishById(dishIdentifier: Long): Dish
    fun getAllDishes(nameFilter: String? = null): List<Dish>
    fun createOrGetDish(dishToProcess: Dish): Pair<Dish, Boolean>
    fun findByName(dishName: String): Dish?
    fun updateDish(dishToModify: Dish): Dish
    fun deleteDishById(dishIdentifier: Long): Boolean
    fun createDishInRestaurant(restaurantId: Long, dish: Dish): Dish
}