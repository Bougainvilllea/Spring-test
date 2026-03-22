package domain.port
import domain.model.Restaurant

interface RestaurantRepositoryPort {
    fun findByName(name: String): Restaurant?
    fun findAll(): List<Restaurant>
    fun findById(id: Long): Restaurant?
    fun update(entity: Restaurant): Restaurant
    fun deleteById(id: Long): Boolean
    fun create(entity: Restaurant): Restaurant
}