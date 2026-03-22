package infrastructure.persistence.jpa.repository


import infrastructure.persistence.jpa.entity.OrderEntity
import org.example.example.domain.model.OrderStatus
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = ["dishes", "user"])
    override fun findById(id: Long): java.util.Optional<OrderEntity>

    @EntityGraph(attributePaths = ["dishes", "user"])
    fun findAllByUserId(userId: Long): List<OrderEntity>

    @EntityGraph(attributePaths = ["dishes", "user"])
    fun findAllByStatus(status: OrderStatus): List<OrderEntity>

    @EntityGraph(attributePaths = ["dishes", "user"])
    fun findAllByUserIdAndStatus(userId: Long, status: OrderStatus): List<OrderEntity>

    @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.dishes d WHERE d.id = :dishId")
    @EntityGraph(attributePaths = ["dishes", "user"])
    fun findAllByDishId(@Param("dishId") dishId: Long): List<OrderEntity>

    @Modifying
    @Query("DELETE FROM OrderEntity o WHERE o.user.id = :userId")
    fun deleteAllByUserId(@Param("userId") userId: Long)
}