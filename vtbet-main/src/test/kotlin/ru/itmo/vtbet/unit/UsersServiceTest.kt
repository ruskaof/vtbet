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
import ru.itmo.vtbet.model.entity.UsersAccountsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.repository.UsersAccountsRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.UsersService
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Optional

class UsersServiceTest {

    private val usersRepository = mock(UsersRepository::class.java)
    private val usersAccountsRepository = mock(UsersAccountsRepository::class.java)
    private val usersService = UsersService(usersRepository, usersAccountsRepository)

    @Test
    fun `get user`() {
        val userId = 1L
        val registrationDate = Instant.now()

        `when`(usersRepository.findById(userId)).thenReturn(Optional.of(UsersEntity(userId, registrationDate)))
        `when`(usersAccountsRepository.findById(userId)).thenReturn(
            Optional.of(
                UsersAccountsEntity(
                    userId = userId,
                    balanceAmount = BigDecimal.TEN,
                    username = "username",
                    email = "email@email.com",
                    phoneNumber = null,
                    accountVerified = true,
                )
            )
        )

        val result = usersService.getUser(userId)
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
        `when`(usersAccountsRepository.save(any())).thenReturn(
            UsersAccountsEntity(
                1L,
                BigDecimal.ZERO,
                "username",
                "email@email.com",
                null,
                true
            )
        )

        usersService.createUser(request)

        verify(usersRepository).save(argThat { it.id == null })
        verify(usersAccountsRepository).save(
            UsersAccountsEntity(
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
        `when`(usersAccountsRepository.findById(userId)).thenReturn(Optional.empty())

        assertThrows(ResourceNotFoundException::class.java) {
            usersService.getUser(userId)
        }
    }

    @Test
    fun `add money to user`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(usersAccountsRepository.findById(userId)).thenReturn(
            Optional.of(
                UsersAccountsEntity(
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
        usersService.addMoneyToUser(userId, amount)

        // then
        verify(usersAccountsRepository).findById(userId)
        verify(usersAccountsRepository).save(
            UsersAccountsEntity(
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

        `when`(usersAccountsRepository.findById(userId)).thenReturn(Optional.empty())

        // then
        assertThrows(ResourceNotFoundException::class.java) {
            usersService.addMoneyToUser(userId, amount)
        }
    }

    @Test
    fun `subtract money from user`() {
        // given
        val userId = 1L
        val amount = BigDecimal.TEN

        `when`(usersAccountsRepository.findById(userId)).thenReturn(
            Optional.of(
                UsersAccountsEntity(
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
        usersService.subtractMoneyFromUser(userId, amount)

        // then
        verify(usersAccountsRepository).findById(userId)
        verify(usersAccountsRepository).save(
            UsersAccountsEntity(
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

        `when`(usersAccountsRepository.findById(userId)).thenReturn(Optional.empty())

        // then
        assertThrows(ResourceNotFoundException::class.java) {
            usersService.subtractMoneyFromUser(userId, amount)
        }
    }

    @Test
    fun `deleteUser test`() {
        val id = 1L
        `when`(usersRepository.findById(id)).thenReturn(Optional.of(UsersEntity(id, OffsetDateTime.now().toInstant())))
        usersService.deleteUser(id)

        verify(usersRepository).deleteById(id)
    }

    @Test
    fun `deleteUser test fail`() {
        val id = 1L
        `when`(usersRepository.findById(id)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { usersService.deleteUser(id) }
    }

    @Test
    fun `updateUser test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")
        val usersEntity = UsersEntity(id, OffsetDateTime.now().toInstant())
        val usersAccountsEntity = UsersAccountsEntity(1, BigDecimal.ONE, "username", "username1", "email1", true)

        `when`(usersRepository.findById(id)).thenReturn(Optional.of(usersEntity))
        `when`(usersAccountsRepository.findById(id)).thenReturn(Optional.of(usersAccountsEntity))

        val updatedUser = usersService.updateUser(id, createUserRequestDto)

        assertEquals(updatedUser.username, createUserRequestDto.username)
        assertEquals(updatedUser.email, createUserRequestDto.email)
        assertEquals(updatedUser.phoneNumber, createUserRequestDto.phoneNumber)

        verify(usersRepository).findById(id)
        verify(usersAccountsRepository).findById(id)
        verify(usersAccountsRepository).saveAndFlush(any())
    }

    @Test
    fun `updateUser user not found test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")

        assertThrows<ResourceNotFoundException> { usersService.updateUser(id, createUserRequestDto) }
    }

    @Test
    fun `updateUser user account not found test`() {
        val id = 1L
        val createUserRequestDto = CreateUserRequestDto("username", "email", "phoneNumber")
        val usersEntity = UsersEntity(id, OffsetDateTime.now().toInstant())

        `when`(usersRepository.findById(id)).thenReturn(Optional.of(usersEntity))

        assertThrows<ResourceNotFoundException> { usersService.updateUser(id, createUserRequestDto) }
    }
}
