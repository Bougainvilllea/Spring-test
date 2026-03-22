package infrastructure.dto.response

import java.math.BigDecimal

data class DishResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean,
    val restaurantId: Long?
)