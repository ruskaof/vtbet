package ru.itmo.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import ru.itmo.auth.model.dto.UserDto
import ru.itmo.auth.model.entity.RolesEntity
import ru.itmo.auth.model.entity.UsersEntity
import ru.itmo.auth.repository.RolesRepository
import ru.itmo.auth.repository.UsersRepository
import ru.itmo.auth.service.AuthService
import ru.itmo.auth.service.JwtService
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.utils.Role
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    lateinit var usersRepository: UsersRepository

    @Mock
    lateinit var rolesRepository: RolesRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var jwtService: JwtService

    @InjectMocks
    lateinit var authService: AuthService

    private val username = "testUser"
    private val password = "password123"
    private val userId = 1L
    private val token = "generatedJwtToken"

    @Test
    fun `should register a new user and return a JwtResponseDto`() {
        // Arrange
        val username = "testuser"
        val request = UserPasswordRequestDto(username = username, password = "password123")
        val encodedPassword = "encodedPassword123"
        val role = RolesEntity(roleId = 1, name = Role.USER)
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )
        val expectedToken = "generatedToken"

        Mockito.`when`(usersRepository.findByUsername(username)).thenReturn(Optional.empty())
        Mockito.`when`(rolesRepository.findByName(Role.USER)).thenReturn(role)
        Mockito.`when`(passwordEncoder.encode(request.password)).thenReturn(encodedPassword)
        Mockito.`when`(usersRepository.saveAndFlush(any())).thenReturn(user)
        Mockito.`when`(jwtService.generateAccessToken(any())).thenReturn(expectedToken)

        // Act
        val result = authService.register(request)

        // Assert
        assertThat(result.userId).isEqualTo(user.userId)
        assertThat(result.jwt).isEqualTo(expectedToken)
    }

    @Test
    fun `should throw exception when username is duplicate`() {
        // Arrange
        val username = "duplicateUser"
        val request = UserPasswordRequestDto(username = "duplicateUser", password = "password123")
        val role = RolesEntity(roleId = 1, name = Role.USER)

        val encodedPassword = "encodedPassword123"
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )

        Mockito.`when`(usersRepository.findByUsername(username)).thenReturn(Optional.of(user))
        // Act & Assert
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            authService.register(request)
        }
    }

    @Test
    fun `should return JWT token when login is successful`() {
        val username = "testuser"
        val request = UserPasswordRequestDto(username = username, password = "password123")
        val encodedPassword = "encodedPassword123"
        val role = RolesEntity(roleId = 1, name = Role.USER)
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )
        // Arrange
        whenever(usersRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user))
        whenever(passwordEncoder.matches(password, user.password)).thenReturn(true)
        whenever(jwtService.generateAccessToken(any())).thenReturn(token)

        // Act
        val result = authService.login(request)

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(userId, result.userId)
        Assertions.assertEquals(token, result.jwt)
        verify(usersRepository).findByUsername(username)
        verify(passwordEncoder).matches(password, user.password)
        verify(jwtService).generateAccessToken(any())
    }

    @Test
    fun `should throw IllegalArgumentException when user not found`() {
        val username = "testuser"
        val request = UserPasswordRequestDto(username = username, password = "password123")
        val encodedPassword = "encodedPassword123"
        val role = RolesEntity(roleId = 1, name = Role.USER)
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )
        // Arrange
        whenever(usersRepository.findByUsername(username)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authService.login(request)
        }

        Assertions.assertEquals("User does not exist", exception.message)
        verify(usersRepository).findByUsername(username)
    }

    @Test
    fun `should throw error when password does not match`() {
        val username = "testuser"
        val request = UserPasswordRequestDto(username = username, password = "password123")
        val encodedPassword = "encodedPassword123"
        val role = RolesEntity(roleId = 1, name = Role.USER)
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )
        // Arrange
        whenever(usersRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user))
        whenever(passwordEncoder.matches(password, user.password)).thenReturn(false)

        // Act & Assert
        val exception = assertThrows<IllegalStateException> {
            authService.login(request)
        }

        assertEquals("invalid username or password", exception.message)
        verify(usersRepository).findByUsername(username)
        verify(passwordEncoder).matches(password, user.password)
    }

    @Test
    fun `should generate JWT token for valid user credentials`() {
        val username = "testuser"
        val request = UserPasswordRequestDto(username = username, password = "password123")
        val encodedPassword = "encodedPassword123"
        val role = RolesEntity(roleId = 1, name = Role.USER)
        val user = UsersEntity(
            userId = 1L,
            username = request.username,
            password = encodedPassword,
            roles = setOf(role)
        )
        // Arrange
//        val role = Role(name = "USER")
        val userDto = UserDto(userId = userId, username = username, roles = setOf(role.name))
        whenever(usersRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user))
        whenever(passwordEncoder.matches(password, user.password)).thenReturn(true)
        whenever(jwtService.generateAccessToken(userDto)).thenReturn(token)

        // Act
        val result = authService.login(request)

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(userId, result.userId)
        Assertions.assertEquals(token, result.jwt)
        verify(jwtService).generateAccessToken(any())
    }
}
