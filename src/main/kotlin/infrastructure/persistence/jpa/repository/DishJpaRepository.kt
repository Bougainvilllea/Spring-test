package infrastructure.persistence.jpa.repository

import infrastructure.persistence.jpa.entity.DishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DishJpaRepository : JpaRepository<DishEntity, Long> {
    @Query("SELECT d FROM DishEntity d WHERE d.isAvailable = true")
    fun findAllAvailable(): List<DishEntity>

    fun findByName(name: String): DishEntity?

    fun findAllByRestaurantId(restaurantId: Long): List<DishEntity>
}