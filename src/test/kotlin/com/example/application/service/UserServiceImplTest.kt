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

    private val sampleUser = User(
        id = 1L,
        email = "test@example.com",
        firstName = "John",
        lastName = "Doe",
        isActive = true
    )

    @Test
    @DisplayName("Should return user by id when exists")
    fun fetchPersonById_ValidId_ReturnsPerson() {
        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)

        val outcome = userService.getUserById(1L)

        assertEquals(sampleUser, outcome)
    }

    @Test
    @DisplayName("Should throw UserNotFound when user does not exist")
    fun fetchPersonById_InvalidId_ThrowsError() {
        `when`(userRepositoryPort.findById(999L)).thenReturn(null)

        assertThrows<BusinessException.UserNotFound> {
            userService.getUserById(999L)
        }
    }

    @Test
    @DisplayName("Should return all users")
    fun fetchAllPersons_ReturnsAllItems() {
        val persons = listOf(sampleUser, sampleUser.copy(id = 2L, email = "other@example.com"))
        `when`(userRepositoryPort.findAll()).thenReturn(persons)

        val outcome = userService.getAllUsers()

        assertEquals(2, outcome.size)
    }

    @Test
    @DisplayName("Should return existing user when creating duplicate email")
    fun saveOrFetchPerson_DuplicateEmail_ReturnsExisting() {
        val newPerson = sampleUser.copy(id = null)
        `when`(userRepositoryPort.findByEmail(sampleUser.email)).thenReturn(sampleUser)

        val (outcome, created) = userService.createOrGetUser(newPerson)

        assertFalse(created)
        assertEquals(sampleUser, outcome)
        verify(userRepositoryPort, never()).create(any())
    }

    @Test
    @DisplayName("Should create new user when email does not exist")
    fun saveOrFetchPerson_NewEmail_CreatesAndReturns() {
        val newPerson = sampleUser.copy(id = null)
        `when`(userRepositoryPort.findByEmail(sampleUser.email)).thenReturn(null)
        `when`(userRepositoryPort.create(newPerson)).thenReturn(sampleUser)

        val (outcome, created) = userService.createOrGetUser(newPerson)

        assertTrue(created)
        assertEquals(sampleUser, outcome)
    }

    @Test
    @DisplayName("Should update user successfully")
    fun modifyPerson_ValidData_UpdatesAndReturns() {
        val updatedPerson = sampleUser.copy(firstName = "Jane")

        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)
        `when`(userRepositoryPort.findByEmail(updatedPerson.email)).thenReturn(null)
        `when`(userRepositoryPort.update(updatedPerson)).thenReturn(updatedPerson)

        val outcome = userService.updateUser(updatedPerson)

        assertEquals("Jane", outcome.firstName)
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate email")
    fun modifyPerson_DuplicateEmail_ThrowsError() {
        val existingPerson = User(id = 2L, email = "existing@example.com", firstName = "Existing", lastName = "User")
        val updatedPerson = sampleUser.copy(email = "existing@example.com")

        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)
        `when`(userRepositoryPort.findByEmail("existing@example.com")).thenReturn(existingPerson)

        assertThrows<BusinessException.EmailAlreadyExists> {
            userService.updateUser(updatedPerson)
        }
    }

    @Test
    @DisplayName("Should delete user successfully")
    fun removePersonById_ValidId_DeletesAndReturnsTrue() {
        `when`(userRepositoryPort.findById(1L)).thenReturn(sampleUser)
        `when`(userRepositoryPort.deleteById(1L)).thenReturn(true)

        val outcome = userService.deleteUserById(1L)

        assertTrue(outcome)
    }
}