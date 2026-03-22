package application.service

import domain.model.User
import domain.port.UserRepositoryPort
import domain.service.UserService
import org.springframework.stereotype.Service
import shared.exception.BusinessException

@Service
class UserService(
    private val userRepositoryPort: UserRepositoryPort
) : UserService {

    override fun getUserById(userId: Long): User {
        return userRepositoryPort.findById(userId)
            ?: throw BusinessException.UserNotFound(userId)
    }

    override fun getAllUsers(): List<User> {
        return userRepositoryPort.findAll()
    }

    override fun findByEmail(email: String): User? {
        return userRepositoryPort.findByEmail(email)
    }

    override fun updateUser(user: User): User {
        val existingUser = userRepositoryPort.findById(user.id!!)
            ?: throw BusinessException.UserNotFound(user.id!!)

        val userWithSameEmail = findByEmail(user.email)
        if (userWithSameEmail != null && userWithSameEmail.id != user.id) {
            throw BusinessException.EmailAlreadyExists(user.email)
        }

        return userRepositoryPort.update(user)
    }

    override fun createOrGetUser(user: User): Pair<User, Boolean> {
        val existingUser = findByEmail(user.email)

        if (existingUser != null) {
            val wasCreated = false
            return Pair(existingUser, wasCreated)
        }

        return try {
            val wasCreated = true
            Pair(userRepositoryPort.create(user), wasCreated)
        } catch (e: IllegalArgumentException) {
            throw BusinessException.UserValidationError()
        }

    }

    override fun deleteUserById(userId: Long): Boolean {
        val userExists = userRepositoryPort.findById(userId)
            ?: throw BusinessException.UserNotFound(userId)

        val result = userRepositoryPort.deleteById(userId)
        return result
    }

}