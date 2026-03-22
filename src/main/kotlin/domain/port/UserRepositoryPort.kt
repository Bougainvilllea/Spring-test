package domain.port
import domain.model.User


interface UserRepositoryPort {
    fun findByEmail(email: String): User?
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun update(entity: User): User
    fun deleteById(id: Long): Boolean
    fun create(entity: User): User
}