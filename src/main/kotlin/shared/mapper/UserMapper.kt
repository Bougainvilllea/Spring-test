package shared.mapper

import org.springframework.stereotype.Component
import org.example.example.infrastructure.dto.requests.user.UserData
import domain.model.User
import application.dto.response.UserResponse
import org.example.example.infrastructure.dto.requests.user.UserUpdateRequest

@Component
class UserMapper {

    fun toResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            isActive = user.isActive
        )
    }

    fun toDomain(userData: UserData): User {
        return User(
            id = null,
            email = userData.email,
            firstName = userData.firstName,
            lastName = userData.lastName,
            isActive = userData.isActive
        )
    }

    fun toDomain(updateRequest: UserUpdateRequest): User {
        return User(
            id = null,
            email = updateRequest.email,
            firstName = updateRequest.firstName,
            lastName = updateRequest.lastName,
            isActive = updateRequest.isActive
        )
    }

    fun toDomain(response: UserResponse): User {
        return User(
            id = response.id,
            email = response.email,
            firstName = response.firstName,
            lastName = response.lastName,
            isActive = response.isActive
        )
    }
}