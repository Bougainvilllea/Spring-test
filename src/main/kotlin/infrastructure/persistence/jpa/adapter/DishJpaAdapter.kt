package infrastructure.persistence.jpa.adapter

import domain.model.Dish
import domain.port.DishRepositoryPort
import infrastructure.persistence.jpa.repository.DishJpaRepository
import infrastructure.persistence.jpa.entity.DishEntity
import infrastructure.persistence.jpa.repository.OrderJpaRepository
import infrastructure.persistence.jpa.repository.RestaurantJpaRepository
import jakarta.transaction.Transactional
import domain.model.Order
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class DishJpaAdapter(
    private val dishJpaRepository: DishJpaRepository,
    private val restaurantJpaRepository: RestaurantJpaRepository,
    private val orderJpaRepository: OrderJpaRepository
) : DishRepositoryPort {

    override fun findAll(): List<Dish> {
        return dishJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: Long): Dish? {
        return dishJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun create(entity: Dish): Dish {
        val restaurantEntity = entity.restaurantId?.let {
            restaurantJpaRepository.findById(it).orElseThrow {
                IllegalArgumentException("Restaurant with id ${entity.restaurantId} not found")
            }
        } ?: throw IllegalArgumentException("Restaurant ID is required for dish creation")

        val dishEntity = DishEntity.fromDomain(entity, restaurantEntity)
        val savedEntity = dishJpaRepository.save(dishEntity)
        return savedEntity.toDomain()
    }

    override fun update(entity: Dish): Dish {
        val existingEntity = dishJpaRepository.findById(entity.id!!)
            .orElseThrow { IllegalArgumentException("Dish with id ${entity.id} not found") }

        val restaurantEntity = existingEntity.restaurant
        val updatedEntity = DishEntity.fromDomain(entity, restaurantEntity)
        val savedEntity = dishJpaRepository.save(updatedEntity)
        return savedEntity.toDomain()
    }

    override fun findByName(name: String): Dish? {
        return dishJpaRepository.findByName(name)?.toDomain()
    }

    override fun findAllByRestaurantId(restaurantId: Long): List<Dish> {
        return dishJpaRepository.findAllByRestaurantId(restaurantId).map { it.toDomain() }
    }

    override fun findOrdersContainingDish(dishId: Long): List<Order> {
        return orderJpaRepository.findAllByDishId(dishId).map { it.toDomain() }
    }

    @Transactional
    override fun deleteById(id: Long): Boolean {
        return if (dishJpaRepository.existsById(id)) {
            val ordersWithDish = orderJpaRepository.findAllByDishId(id)

            if (ordersWithDish.isEmpty()) {
                dishJpaRepository.deleteById(id)
                true
            } else {
                false
            }
        } else {
            false
        }
    }
}