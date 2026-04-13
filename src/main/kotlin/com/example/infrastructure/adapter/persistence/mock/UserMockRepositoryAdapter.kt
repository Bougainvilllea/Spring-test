package com.example.infrastructure.adapter.persistence.mock

import com.example.domain.model.User
import com.example.domain.port.UserRepositoryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("mock")
class UserMockRepositoryAdapter : UserRepositoryPort, BaseMockRepositoryAdapter<User>() {
    override fun findByEmail(email: String): User? {
        return storage.values.find { it.email.equals(email, ignoreCase = true) }
    }
}