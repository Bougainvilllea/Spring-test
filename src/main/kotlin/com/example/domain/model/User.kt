package com.example.domain.model

import com.example.domain.model.common.IHaveID

/** Оно же должно быть неизменяемым, да?*/


data class User(
    override var id: Long? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
) : IHaveID<User>
{
    init {
        require(firstName.isNotEmpty()) { "First name must not be empty" }
        require(lastName.isNotEmpty()) { "Last name must not be empty" }
        require(email.isNotEmpty()) { "Email must not be empty" }
        require(email.contains("@")) { "Email must be valid" }
    }

    fun activate(): User {
        return this.copy(isActive = true)
    }

    fun deactivate(): User {
        return this.copy(isActive = false)
    }

    fun getFullName(): String {
        return "$lastName $firstName"
    }

    override fun withId(id: Long): User {
        return this.copy(id=id)
    }
}


