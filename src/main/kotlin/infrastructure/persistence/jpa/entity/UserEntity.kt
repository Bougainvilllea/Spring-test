package infrastructure.persistence.jpa.entity
import domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(name = "first_name", nullable = false)
    val firstName: String,

    @Column(name = "last_name", nullable = false)
    val lastName: String,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orders: MutableList<OrderEntity> = mutableListOf()

) {
    // Конструктор без параметров для JPA
    constructor() : this(null, "", "", "", true)

    // Преобразование в доменную модель
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            isActive = isActive
        )
    }

    companion object {
        // Создание Entity из доменной модели
        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                isActive = user.isActive
            )
        }
    }
}