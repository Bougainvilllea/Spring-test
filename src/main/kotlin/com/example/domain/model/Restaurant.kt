package com.example.domain.model

import com.example.domain.model.common.IHaveID

data class Restaurant(
    override var id: Long? = null,
    val name: String,
    val address: String
) : IHaveID<Restaurant> {
    init {
        require(name.isNotEmpty()) { "Restaurant name must not be empty" }
        require(address.isNotEmpty()) { "Restaurant address must not be empty" }
    }

    override fun withId(id: Long): Restaurant {
        return this.copy(id = id)
    }
}