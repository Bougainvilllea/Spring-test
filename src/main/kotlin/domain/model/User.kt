package domain.model

data class User(
    var id: Long? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true,

)
{
    init {
        require(firstName.isNotEmpty()) { "First name must not be empty" }
        require(lastName.isNotEmpty()) { "Last name must not be empty" }
        require(email.isNotEmpty()) { "Email must not be empty" }
        require(email.contains("@")) { "Email must be valid" }
    }

    fun withId(id: Long): User {
        return this.copy(id = id)
    }
}


