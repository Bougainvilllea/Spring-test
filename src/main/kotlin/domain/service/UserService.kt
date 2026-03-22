package domain.service

import domain.model.User

interface UserService {
    fun getUserById(userIdentifier: Long): User
    fun getAllUsers(): List<User>
    fun createOrGetUser(userToProcess: User): Pair<User, Boolean>
    fun findByEmail(emailAddress: String): User?
    fun updateUser(userToModify: User): User
    fun deleteUserById(userIdentifier: Long): Boolean
}