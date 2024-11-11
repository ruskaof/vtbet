package ru.itmo.user.accounter.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import reactor.core.publisher.Mono
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserAccountDto
import ru.itmo.common.entity.RolesEntity
import ru.itmo.common.entity.UsersAccountsEntity
import ru.itmo.common.entity.UsersEntity
import ru.itmo.user.accounter.repository.UsersAccountsRepository
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.toDto
import java.math.BigDecimal
import java.time.Instant

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
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        ).toDto()
        val usersAccountsEntityDto = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntityDto))


        val result = usersAccountsService.getUserAccount(userId).block()

        val expectedResult = UserAccountDto(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
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
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        ).toDto()
        val usersAccountsEntityDto = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val complexUserDto = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntityDto))

        val result = usersAccountsService.save(complexUserDto).block()

        val expectedResult = usersAccountsEntityDto.toDto()

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
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        ).toDto()
        val usersAccountsEntityDto = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val complexUserDto = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntityDto))


        val result = usersAccountsService.update(complexUserDto).block()

        val expectedResult = usersAccountsEntityDto.toDto()

        Assertions.assertEquals(expectedResult, result)
    }
}
