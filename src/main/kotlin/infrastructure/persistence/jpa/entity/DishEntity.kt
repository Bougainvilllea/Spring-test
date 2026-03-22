package infrastructure.persistence.jpa.entity

import domain.model.Dish
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "dishes")
data class DishEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, length = 1000)
    val description: String,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "is_available", nullable = false)
    val isAvailable: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    val restaurant: RestaurantEntity
) {
    constructor() : this(
        null, "", "", BigDecimal.ZERO, true,
        RestaurantEntity()
    )

    fun toDomain(): Dish {
        return Dish(
            id = id,
            name = name,
            description = description,
            price = price,
            isAvailable = isAvailable,
            restaurantId = restaurant.id
        )
    }

    companion object {
        fun fromDomain(dish: Dish, restaurantEntity: RestaurantEntity): DishEntity {
            return DishEntity(
                id = dish.id,
                name = dish.name,
                description = dish.description,
                price = dish.price,
                isAvailable = dish.isAvailable,
                restaurant = restaurantEntity
            )
        }
    }
}