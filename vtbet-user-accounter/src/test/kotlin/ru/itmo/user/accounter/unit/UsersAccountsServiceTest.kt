package ru.itmo.user.accounter.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.any
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.model.entity.UsersEntity
import ru.itmo.user.accounter.repository.UsersAccountsRepository
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.toDto
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
            userId = userId,
            balanceAmount = balanceAmount,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))


        val result = usersAccountsService.getUserAccount(userId).block()

        val expectedResult = UserAccountDto(
            accountId = accountId,
            balanceAmount = balanceAmount,
        )

        Assertions.assertEquals(expectedResult, result)
    }

//    @Test
//    fun `get complex user account`() {
//        val userId = 1L
//        val accountId = 1L
//        val registrationDate = Instant.now()
//        val username = "Dima"
//        val email = "niktog1@mail.ru"
//        val accountVerified = true
//        val phoneNumber = "79991035532"
//        val balanceAmount = BigDecimal(1000)
//        val usersEntity = UsersEntity(
//            userId = userId,
//            username = username,
//            email = email,
//            phoneNumber = phoneNumber,
//            accountVerified = accountVerified,
//            registrationDate = registrationDate,
//        )
//        val usersAccountsEntity = UsersAccountsEntity(
//            accountId = accountId,
//            userId = userId,
//            balanceAmount = balanceAmount,
//        )
//
//        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
//
//
//        val result = usersAccountsService.getComplexUserAccount(userId)
//
//        val expectedResult = ComplexUserDto(
//            userId = userId,
//            accountId = accountId,
//            registrationDate = registrationDate,
//            balanceAmount = balanceAmount,
//            username = username,
//            email = email,
//            phoneNumber = phoneNumber,
//            accountVerified = accountVerified,
//        )
//
//        Assertions.assertEquals(expectedResult, result)
//    }

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
            userId = userId,
            balanceAmount = balanceAmount,
        )

        val complexUserDto = ComplexUserDto(
            userId = userId,
            accountId = accountId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))




        val result = usersAccountsService.save(complexUserDto).block()

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
            userId = userId,
            balanceAmount = balanceAmount,
        )

        val complexUserDto = ComplexUserDto(
            userId = userId,
            accountId = accountId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))


        val result = usersAccountsService.update(complexUserDto).block()

        val expectedResult = usersAccountsEntity.toDto()

        Assertions.assertEquals(expectedResult, result)
    }

}