package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.UserService
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class UserServiceTest {

    private val usersRepository = mock(UsersRepository::class.java)
    private val userAccountRepository = mock(UserAccountRepository::class.java)
    private val userService = UserService(usersRepository, userAccountRepository)

    @Test
    fun `get user`() {
        // given
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

        // when
        val result = userService.getUser(userId)

        // then
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
        // given
        val request = CreateUserRequest(
            username = "username",
            email = "email@email.com",
            phoneNumber = null,
        )
        `when`(usersRepository.save(any())).thenReturn(UsersEntity(1L, Instant.now()))
        `when`(userAccountRepository.save(any())).thenReturn(UserAccountEntity(1L, BigDecimal.ZERO, "username", "email@email.com", null, true))

        // when
        userService.createUser(request)

        // then
        verify(usersRepository).save(argThat { it.id == null})
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
        // given
        val userId = 1L

        `when`(usersRepository.findById(userId)).thenReturn(Optional.empty())
        `when`(userAccountRepository.findById(userId)).thenReturn(Optional.empty())

        // then
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
}
