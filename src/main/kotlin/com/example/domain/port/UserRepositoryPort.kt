package com.example.domain.port

import com.example.domain.model.User

interface UserRepositoryPort : BaseRepositoryPort<User> {
    fun findByEmail(email: String): User?
}