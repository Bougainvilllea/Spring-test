package com.example.application.service

import com.example.domain.model.User
import com.example.domain.port.UserRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import com.example.shared.exception.BusinessException
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    private val testUser = User(
        id = 1L,
        email = "test@example.com",
        firstName = "John",
        lastName = "Doe",
        isActive = true
    )

    @Test
    @DisplayName("Should return user by id when exists")
    fun `should return user by id when exists`() {
        `when`(userRepositoryPort.findById(1L)).thenReturn(testUser)

        val result = userService.getUserById(1L)

        assertEquals(testUser, result)
    }

    @Test
    @DisplayName("Should throw UserNotFound when user does not exist")
    fun `should throw UserNotFound when user does not exist`() {
        `when`(userRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.UserNotFound> {
            userService.getUserById(999L)
        }
    }

    @Test
    @DisplayName("Should return all users")
    fun `should return all users`() {
        val users = listOf(testUser, testUser.copy(id = 2L, email = "other@example.com"))
        `when`(userRepositoryPort.findAll()).thenReturn(users)

        val result = userService.getAllUsers()

        assertEquals(2, result.size)
    }

    @Test
    @DisplayName("Should return existing user when creating duplicate email")
    fun `should return existing user when creating duplicate email`() {
        val newUser = testUser.copy(id = null)
        `when`(userRepositoryPort.findByEmail(testUser.email)).thenReturn(testUser)

        val (result, wasCreated) = userService.createOrGetUser(newUser)

        assertFalse(wasCreated)
        assertEquals(testUser, result)
        verify(userRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new user when email does not exist")
    fun `should create new user when email does not exist`() {
        val newUser = testUser.copy(id = null)
        `when`(userRepositoryPort.findByEmail(testUser.email)).thenReturn(null)
        `when`(userRepositoryPort.create(newUser)).thenReturn(testUser)

        val (result, wasCreated) = userService.createOrGetUser(newUser)

        assertTrue(wasCreated)
        assertEquals(testUser, result)
    }

    @Test
    @DisplayName("Should update user successfully")
    fun `should update user successfully`() {
        val updatedUser = testUser.copy(firstName = "Jane")

        `when`(userRepositoryPort.findById(1L)).thenReturn(testUser)
        `when`(userRepositoryPort.findByEmail(updatedUser.email)).thenReturn(null)
        `when`(userRepositoryPort.update(updatedUser)).thenReturn(updatedUser)

        val result = userService.updateUser(updatedUser)

        assertEquals("Jane", result.firstName)
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate email")
    fun `should throw exception when updating with duplicate email`() {
        val existingUser = User(id = 2L, email = "existing@example.com", firstName = "Existing", lastName = "User")
        val updatedUser = testUser.copy(email = "existing@example.com")

        `when`(userRepositoryPort.findById(1L)).thenReturn(testUser)
        `when`(userRepositoryPort.findByEmail("existing@example.com")).thenReturn(existingUser)

        assertThrows<BusinessException.EmailAlreadyExists> {
            userService.updateUser(updatedUser)
        }
    }

    @Test
    @DisplayName("Should delete user successfully")
    fun `should delete user successfully`() {
        `when`(userRepositoryPort.findById(1L)).thenReturn(testUser)
        `when`(userRepositoryPort.deleteById(1L)).thenReturn(true)

        val result = userService.deleteUserById(1L)

        assertTrue(result)
    }
}