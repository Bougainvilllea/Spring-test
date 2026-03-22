package infrastructure.persistence.jpa.entity
import domain.model.Restaurant
import jakarta.persistence.*


@Entity
@Table(name = "restaurants")
data class RestaurantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val address: String,

    @OneToMany(mappedBy = "restaurant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val dishes: MutableList<DishEntity> = mutableListOf()
) {
    constructor() : this(
        id = null,
        name = "",
        address = ""
    )

    fun toDomain(): Restaurant {
        return Restaurant(
            id = id,
            name = name,
            address = address
        )
    }

    companion object {
        fun fromDomain(restaurant: Restaurant): RestaurantEntity {
            return RestaurantEntity(
                id = restaurant.id,
                name = restaurant.name,
                address = restaurant.address
            )
        }
    }
}