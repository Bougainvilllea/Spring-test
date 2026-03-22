package infrastructure.dto.requests.dish

import java.math.BigDecimal

data class DishUpdateRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean,
    val restaurantId: Long
)