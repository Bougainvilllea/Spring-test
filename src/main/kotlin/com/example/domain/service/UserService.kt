package com.example.domain.service

import com.example.domain.model.User

interface UserService {
    fun getUserById(userId: Long): User
    fun getAllUsers(): List<User>
    fun createOrGetUser(user: User): Pair<User, Boolean> // пользователь, True: был создан False: получен из репозитория
    fun findByEmail(email: String): User?
    fun updateUser(user: User): User
    fun deleteUserById(userId: Long) : Boolean
}