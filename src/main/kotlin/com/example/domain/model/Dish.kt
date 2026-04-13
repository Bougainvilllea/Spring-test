package com.example.domain.model

import com.example.domain.model.common.IHaveID
import java.math.BigDecimal

data class Dish(
    override var id: Long? = null,
    var name: String,
    var description: String? = null,
    val price: BigDecimal,
    val isAvailable: Boolean,
    val restaurantId: Long?
): IHaveID<Dish>
{
    init
    {
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(price >= BigDecimal.ZERO) { "New price must be greater than zero" }
    }

    fun makeAvailable(): Dish {
        return this.copy(isAvailable = true)
    }

    fun makeUnavailable(): Dish {
        return this.copy(isAvailable = false)
    }

    fun updatePrice(newPrice: BigDecimal): Dish {
        require(newPrice >= BigDecimal.ZERO) { "New price must be greater than zero" }
        return this.copy(price = newPrice)
    }

    override fun withId(id: Long): Dish {
        return this.copy(id = id)
    }

}
