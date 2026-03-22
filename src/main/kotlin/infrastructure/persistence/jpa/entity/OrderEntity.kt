package infrastructure.persistence.jpa.entity
import jakarta.persistence.*
import java.time.LocalDateTime
import domain.model.Order
import org.example.example.domain.model.OrderStatus as DomainOrderStatus

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: DomainOrderStatus = DomainOrderStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "order_dishes",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "dish_id")]
    )
    val dishes: MutableList<DishEntity> = mutableListOf()
) {
    constructor() : this(
        id = null,
        status = DomainOrderStatus.PENDING,
        createdAt = LocalDateTime.now(),
        user = UserEntity(),
        dishes = mutableListOf()
    )

    fun toDomain(): Order {
        return Order(
            id = id,
            userId = user.id ?: throw IllegalStateException("User ID is null"),
            status = status,
            createdAt = createdAt,
            dishes = dishes.map { it.toDomain() }
        )
    }

    companion object {
        fun fromDomain(order: Order, userEntity: UserEntity, dishEntities: List<DishEntity>): OrderEntity {
            return OrderEntity(
                id = order.id,
                status = order.status,
                createdAt = order.createdAt,
                user = userEntity,
                dishes = dishEntities.toMutableList()
            )
        }
    }
}