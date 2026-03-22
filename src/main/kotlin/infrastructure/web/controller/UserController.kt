package infrastructure.web.controller

import org.example.example.infrastructure.dto.requests.user.UserData
import application.dto.response.UserResponse
import domain.model.User
import domain.service.UserService
import org.example.example.infrastructure.dto.requests.user.UserUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shared.mapper.UserMapper


@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping
    fun listUsers(): ResponseEntity<List<UserResponse>> {
        val usersDomain: List<User> = userService.getAllUsers()
        val response: List<UserResponse> = usersDomain.map { userMapper.toResponse(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createUser(@RequestBody userData: UserData): ResponseEntity<UserResponse> {
        val userFromData = userMapper.toDomain(userData)
        val (resultUser, wasCreated) = userService.createOrGetUser(userFromData)
        val response = userMapper.toResponse(resultUser)

        val status = if (wasCreated) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Int): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id.toLong())
        val response = userMapper.toResponse(user)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Int, @RequestBody updateRequest: UserUpdateRequest): ResponseEntity<UserResponse> {
        val user = userMapper.toDomain(updateRequest).copy(id = id.toLong())
        val updatedUser = userService.updateUser(user)
        val response = userMapper.toResponse(updatedUser)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Int): ResponseEntity<Boolean> {
        val result = userService.deleteUserById(id.toLong())
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result)
    }
}