package domain.model


data class Restaurant(
    var id: Long? = null,
    val name: String,
    val address: String
)  {
    init {
        require(name.isNotEmpty()) { "Restaurant name must not be empty" }
        require(address.isNotEmpty()) { "Restaurant address must not be empty" }
    }
}