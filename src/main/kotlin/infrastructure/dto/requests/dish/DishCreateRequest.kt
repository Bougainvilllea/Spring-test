package infrastructure.dto.requests.dish
import java.math.BigDecimal

data class DishCreateRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean = true,
    val restaurantId: Long
)