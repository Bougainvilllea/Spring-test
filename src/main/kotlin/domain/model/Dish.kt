package domain.model

import java.math.BigDecimal

data class Dish(
    var id: Long? = null,
    var name: String,
    var description: String,
    val price: BigDecimal,
    val isAvailable: Boolean,
    val restaurantId: Long?
)
{
    init
    {
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(description.isNotEmpty()) { "Description must not be empty" }
        require(price >= BigDecimal.ZERO) { "New price must be greater than zero" }
    }

    fun withId(id: Long): Dish {
        return this.copy(id = id)
    }

}
