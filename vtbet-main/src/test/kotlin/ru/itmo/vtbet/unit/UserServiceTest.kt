package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.any
import org.mockito.Mockito.argThat
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.UserService
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Optional

class UserServiceTest {

    private val usersRepository = mock(UsersRepository::class.java)
    private val userAccountRepository = mock(UserAccountRepository::class.java)
    private val userService = UserService(usersRepository, userAccountRepository)

    @Test
    fun `get user`() {
        val userId = 1L
        val registrationDate = Instant.now()

        `when`(usersRepository.findById(userId)).thenReturn(Optional.of(UsersEntity(userId, registrationDate)))
        `when`(userAccountRepository.findById(userId)).thenReturn(
            Optional.of(
                UserAccountEntity(
                    userId = userId,
                    balanceAmount = BigDecimal.TEN,
                    username = "username",
                    email = "email@email.com",
                    phoneNumber = null,
                    accountVerified = true,
                )
            )
        )

        val result = userService.getUser(userId)
        val expectedResult = UserDto(
            id = userId,
            registrationDate = registrationDate,
            balanceAmount = BigDecimal.TEN,
            username = "username",
            email = "email@email.com",
            phoneNumber = null,
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `create user`() {
        val request = CreateUserRequestDto(
            username = "username",
            email = "email@email.com",
            phoneNumber = null,
        )
        `when`(usersRepository.save(any())).thenReturn(UsersEntity(1L, Instant.now()))
        `when`(userAccountRepository.save(any())).thenReturn(
            UserAccountEntity(
                1L,
                BigDecimal.ZERO,
                "username",
                "email@email.com",
                null,
                true
            )
        )

        userService.createUser(request)

        verify(usersRepository).save(argThat { it.id == null })
        verify(userAccountRepository).save(
            UserAccountEntity(
                userId = 1L,
                balanceAmount = BigDecimal.ZERO,
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
                accountVerified = true,
            )
        )
    }

    @Test
    fun `get user throws exception when user not found`() {
        val userId = 1L

        `when`(usersRepository.findById(userId)).thenReturn(Optional.empty())
        `when`(userAccountRepository.findById(userId)).thenReturn(Optional.empty())

        assertThrows(ResourceNotFoundException::class.java) {
            userService.getUser(userId)
        }
    }

    @Test
    fun `add money to user`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(userAccountRepository.findById(userId)).thenReturn(
            Optional.of(
                UserAccountEntity(
                    userId = userId,
                    balanceAmount = BigDecimal.ZERO,
                    username = "username",
                    email = "email@email.com",
                    phoneNumber = null,
                    accountVerified = true,
                )
            )
        )

        // when
        userService.addMoneyToUser(userId, amount)

        // then
        verify(userAccountRepository).findById(userId)
        verify(userAccountRepository).save(
            UserAccountEntity(
                userId = userId,
                balanceAmount = BigDecimal.TEN,
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
                accountVerified = true,
            )
        )
    }

    @Test
    fun `add money to user throws exception when user not found`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(userAccountRepository.findById(userId)).thenReturn(Optional.empty())

        // then
        assertThrows(ResourceNotFoundException::class.java) {
            userService.addMoneyToUser(userId, amount)
        }
    }

    @Test
    fun `subtract money from user`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(userAccountRepository.findById(userId)).thenReturn(
            Optional.of(
                UserAccountEntity(
                    userId = userId,
                    balanceAmount = BigDecimal.TEN,
                    username = "username",
                    email = "email@email.com",
                    phoneNumber = null,
                    accountVerified = true,
                )
            )
        )

        // when
        userService.subtractMoneyFromUser(userId, amount)

        // then
        verify(userAccountRepository).findById(userId)
        verify(userAccountRepository).save(
            UserAccountEntity(
                userId = userId,
                balanceAmount = BigDecimal.ZERO,
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
                accountVerified = true,
            )
        )
    }

    @Test
    fun `subtract money from user throws exception when user not found`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(userAccountRepository.findById(userId)).thenReturn(Optional.empty())

        // then
        assertThrows(ResourceNotFoundException::class.java) {
            userService.subtractMoneyFromUser(userId, amount)
        }
    }

    @Test
    fun `deleteUser test`() {
        val id = 1L
        `when`(usersRepository.findById(id)).thenReturn(Optional.of(UsersEntity(id, OffsetDateTime.now().toInstant())))
        userService.deleteUser(id)

        verify(usersRepository).deleteById(id)
    }

    @Test
    fun `deleteUser test fail`() {
        val id = 1L
        `when`(usersRepository.findById(id)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { userService.deleteUser(id) }
    }

    @Test
    fun `updateUser test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")
        val usersEntity = UsersEntity(id, OffsetDateTime.now().toInstant())
        val userAccountEntity = UserAccountEntity(1, BigDecimal.ONE, "username", "username1", "email1", true)

        `when`(usersRepository.findById(id)).thenReturn(Optional.of(usersEntity))
        `when`(userAccountRepository.findById(id)).thenReturn(Optional.of(userAccountEntity))

        val updatedUser = userService.updateUser(id, createUserRequestDto)

        assertEquals(updatedUser.username, createUserRequestDto.username)
        assertEquals(updatedUser.email, createUserRequestDto.email)
        assertEquals(updatedUser.phoneNumber, createUserRequestDto.phoneNumber)

        verify(usersRepository).findById(id)
        verify(userAccountRepository).findById(id)
        verify(userAccountRepository).saveAndFlush(any())
    }

    @Test
    fun `updateUser user not found test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")

        assertThrows<ResourceNotFoundException> { userService.updateUser(id, createUserRequestDto) }
    }

    @Test
    fun `updateUser user account not found test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")
        val usersEntity = UsersEntity(id, OffsetDateTime.now().toInstant())

        `when`(usersRepository.findById(id)).thenReturn(Optional.of(usersEntity))

        assertThrows<ResourceNotFoundException> { userService.updateUser(id, createUserRequestDto) }
    }
}
