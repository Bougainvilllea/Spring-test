package infrastructure.persistence.jpa.repository

import infrastructure.persistence.jpa.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, Long> {
    fun findByName(name: String): RestaurantEntity?
}