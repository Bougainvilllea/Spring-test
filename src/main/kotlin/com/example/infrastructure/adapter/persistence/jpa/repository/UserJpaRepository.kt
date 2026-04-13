package com.example.infrastructure.adapter.persistence.jpa.repository

import com.example.infrastructure.adapter.persistence.jpa.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
}