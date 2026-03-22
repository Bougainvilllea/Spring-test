package infrastructure.persistence.jpa.adapter


import infrastructure.persistence.jpa.repository.DishJpaRepository
import infrastructure.persistence.jpa.repository.OrderJpaRepository
import infrastructure.persistence.jpa.repository.UserJpaRepository
import domain.model.Order
import domain.port.OrderRepositoryPort
import infrastructure.persistence.jpa.entity.OrderEntity
import org.example.example.domain.model.OrderStatus
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class OrderJpaAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val dishJpaRepository: DishJpaRepository
) : OrderRepositoryPort {

    override fun findAll(): List<Order> {
        return orderJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: Long): Order? {
        return orderJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun create(entity: Order): Order {
        val userEntity = userJpaRepository.findById(entity.userId)
            .orElseThrow { IllegalArgumentException("User with id ${entity.userId} not found") }

        val dishEntities = entity.dishes.map { dish ->
            dishJpaRepository.findById(dish.id!!)
                .orElseThrow { IllegalArgumentException("Dish with id ${dish.id} not found") }
        }

        val orderEntity = OrderEntity.fromDomain(entity, userEntity, dishEntities)
        val savedEntity = orderJpaRepository.save(orderEntity)
        return savedEntity.toDomain()
    }

    override fun update(entity: Order): Order {
        val existingEntity = orderJpaRepository.findById(entity.id!!)
            .orElseThrow { IllegalArgumentException("Order with id ${entity.id} not found") }

        val userEntity = userJpaRepository.findById(entity.userId)
            .orElseThrow { IllegalArgumentException("User with id ${entity.userId} not found") }

        val dishEntities = entity.dishes.map { dish ->
            dishJpaRepository.findById(dish.id!!)
                .orElseThrow { IllegalArgumentException("Dish with id ${dish.id} not found") }
        }

        val updatedEntity = OrderEntity.fromDomain(entity, userEntity, dishEntities)
        val savedEntity = orderJpaRepository.save(updatedEntity)
        return savedEntity.toDomain()
    }

    override fun deleteById(id: Long): Boolean {
        return if (orderJpaRepository.existsById(id)) {
            orderJpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    override fun findAllByUserId(userId: Long): List<Order> {
        return orderJpaRepository.findAllByUserId(userId).map { it.toDomain() }
    }

    override fun findAllByStatus(status: OrderStatus): List<Order> {
        return orderJpaRepository.findAllByStatus(status).map { it.toDomain() }
    }

    override fun findAllByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order> {
        return orderJpaRepository.findAllByUserIdAndStatus(userId, status).map { it.toDomain() }
    }

    override fun findAllByDishId(dishId: Long): List<Order> {
        return orderJpaRepository.findAllByDishId(dishId).map { it.toDomain() }
    }

    override fun existsOrderWithDish(dishId: Long): Boolean {
        TODO("Not yet implemented")
    }
}