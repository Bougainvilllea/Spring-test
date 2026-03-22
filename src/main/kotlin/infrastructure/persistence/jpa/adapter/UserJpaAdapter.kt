package infrastructure.persistence.jpa.adapter

import domain.model.User
import domain.port.UserRepositoryPort
import infrastructure.persistence.jpa.repository.UserJpaRepository
import infrastructure.persistence.jpa.entity.UserEntity
import infrastructure.persistence.jpa.repository.OrderJpaRepository
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("db")
class UserJpaAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val orderJpaRepository: OrderJpaRepository
) : UserRepositoryPort {

    override fun findAll(): List<User> {
        return userJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: Long): User? {
        return userJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun create(entity: User): User {
        val userEntity = UserEntity.fromDomain(entity)
        val savedEntity = userJpaRepository.save(userEntity)
        return savedEntity.toDomain()
    }

    override fun update(entity: User): User {
        val existingEntity = userJpaRepository.findById(entity.id!!)
            .orElseThrow { IllegalArgumentException("User with id ${entity.id} not found") }

        val updatedEntity = UserEntity.fromDomain(entity)
        val savedEntity = userJpaRepository.save(updatedEntity)
        return savedEntity.toDomain()
    }

    @Transactional
    override fun deleteById(id: Long): Boolean {
        return if (userJpaRepository.existsById(id)) {
            val userOrders = orderJpaRepository.findAllByUserId(id)
            if (userOrders.isNotEmpty()) {
                orderJpaRepository.deleteAll(userOrders)
            }
            userJpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)?.toDomain()
    }

}