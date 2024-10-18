package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import ru.itmo.vtbet.model.dto.ComplexUserDto
import ru.itmo.vtbet.model.dto.UserAccountDto
import org.mockito.ArgumentMatchers.any
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.UsersAccountsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.repository.UsersAccountsRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.UsersAccountsService
import ru.itmo.vtbet.service.UsersService
import ru.itmo.vtbet.service.toComplexDto
import ru.itmo.vtbet.service.toDto
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class UsersAccountsServiceTest {
    private val usersAccountsRepository = Mockito.mock(UsersAccountsRepository::class.java)
    private val usersAccountsService = UsersAccountsService(usersAccountsRepository)


    @Test
    fun `get user account`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val usersAccountsEntity = UsersAccountsEntity(
            accountId = accountId,
            usersEntity = usersEntity,
            balanceAmount = balanceAmount,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Optional.of(usersAccountsEntity))


        val result = usersAccountsService.getUserAccount(userId)

        val expectedResult = UserAccountDto(
            userId = userId,
            accountId = accountId,
            balanceAmount = balanceAmount,
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get complex user account`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val usersAccountsEntity = UsersAccountsEntity(
            accountId = accountId,
            usersEntity = usersEntity,
            balanceAmount = balanceAmount,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Optional.of(usersAccountsEntity))


        val result = usersAccountsService.getComplexUserAccount(userId)

        val expectedResult = ComplexUserDto(
            userId = userId,
            accountId = accountId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `save`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val usersAccountsEntity = UsersAccountsEntity(
            accountId = accountId,
            usersEntity = usersEntity,
            balanceAmount = balanceAmount,
        )

        Mockito.`when`(usersAccountsRepository.saveAndFlush(any())).thenReturn((usersAccountsEntity))


        val result = usersAccountsService.save(usersAccountsEntity.toComplexDto())

        val expectedResult = usersAccountsEntity.toDto()

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `update`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val usersAccountsEntity = UsersAccountsEntity(
            accountId = accountId,
            usersEntity = usersEntity,
            balanceAmount = balanceAmount,
        )

        Mockito.`when`(usersAccountsRepository.saveAndFlush(any())).thenReturn((usersAccountsEntity))


        val result = usersAccountsService.update(usersAccountsEntity.toComplexDto())

        val expectedResult = usersAccountsEntity.toDto()

        Assertions.assertEquals(expectedResult, result)
    }

}