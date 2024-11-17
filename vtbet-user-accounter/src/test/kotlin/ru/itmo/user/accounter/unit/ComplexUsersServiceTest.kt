package ru.itmo.user.accounter.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import reactor.core.publisher.Mono
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.entity.RolesEntity
import ru.itmo.common.entity.UsersAccountsEntity
import ru.itmo.common.entity.UsersEntity
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.user.accounter.service.ComplexUsersService
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.UsersOperationsService
import ru.itmo.user.accounter.service.toDto
import java.math.BigDecimal
import java.time.Instant

class ComplexUsersServiceTest {

    private val usersOperationsService = Mockito.mock(UsersOperationsService::class.java)
    private val usersAccountsService = Mockito.mock(UsersAccountsService::class.java)
    private val ratioDecreaseValue = BigDecimal(0.01)

    private val complexUsersService = ComplexUsersService(
        usersOperationsService,
        usersAccountsService,
        ratioDecreaseValue,
    )

    @Test
    fun `get user`() {
        val userId = 1L
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
        ).toDto()

        Mockito.`when`(usersOperationsService.getUser(userId)).thenReturn(Mono.just(usersEntityDto))
        Mockito.`when`(usersAccountsService.getUserAccount(userId)).thenReturn(Mono.just(usersAccountsEntityDto))

        val result = complexUsersService.getUser(userId).block()

        val expectedResult = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Assertions.assertEquals(expectedResult, result)
    }


    @Test
    fun `create user`() {
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
        ).toDto()

        val complexUserDto = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        val createUserRequestDTO = CreateUserRequestDto(
            username,
            email,
            phoneNumber,
            password = "1234",
        )

        Mockito.`when`(usersOperationsService.getByUserName(username)).thenReturn(Mono.empty())
        Mockito.`when`(usersOperationsService.save(MockitoHelper.anyObject())).thenReturn(Mono.just(usersEntityDto))
        Mockito.`when`(usersAccountsService.createUserAccount(MockitoHelper.anyObject(), userId))
            .thenReturn(Mono.just(usersAccountsEntityDto))

        val result = complexUsersService.createUser(createUserRequestDTO).block()

        val expectedResult = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `delete user`() {
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

        Mockito.`when`(usersOperationsService.getUser(userId)).thenReturn(Mono.just(usersEntityDto))
        Mockito.`when`(usersOperationsService.deleteById(userId)).thenReturn(Mono.empty())


        complexUsersService.deleteUser(userId).block()


        verify(usersOperationsService).deleteById(userId)
    }

    @Test
    fun `update user`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val username2 = "Dima2"
        val email2 = "niktog2@mail.ru"
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
        ).toDto()

        val complexUserDto = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        val updateUserRequestDto = UpdateUserRequestDto(
            email2,
            phoneNumber,
            true,
        )

        Mockito.`when`(usersOperationsService.getUser(userId)).thenReturn(Mono.just(usersEntityDto))
        Mockito.`when`(usersAccountsService.getUserAccount(userId))
            .thenReturn(Mono.just(usersAccountsEntityDto))
            .thenReturn(Mono.just(usersAccountsEntityDto.copy(email = email2)))
        Mockito.`when`(usersAccountsService.updateUserAccount(complexUserDto.copy(email = email2), userId))
            .thenReturn(Mono.just(usersAccountsEntityDto.copy(email = email2)))


        val result = complexUsersService.updateUser(userId, updateUserRequestDto).block()

        val expectedResult = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = balanceAmount,
            username = username,
            email = email2,
            phoneNumber = phoneNumber,
            true,
        )

        Assertions.assertEquals(expectedResult, result)
    }
//  expected: <ComplexUserDto(userId=1, registrationDate=2024-11-11T21:19:13.609411Z, balanceAmount=1000, username=Dima, email=niktog2@mail.ru, phoneNumber=79991035532, accountVerified=true)>
//   but was: <ComplexUserDto(userId=1, registrationDate=2024-11-11T21:19:13.609411Z, balanceAmount=1000, username=Dima, email=niktog1@mail.ru, phoneNumber=79991035532, accountVerified=true)>
//	at org.junit.jupiter.api.AssertionFailureBuilder
    @Test
    fun `handle balance action`() {
        val userId = 1L
        val accountId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val username2 = "Dima2"
        val email2 = "niktog2@mail.ru"
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
        ).toDto()


        Mockito.`when`(usersOperationsService.getUser(userId)).thenReturn(Mono.just(usersEntityDto))
        Mockito.`when`(usersAccountsService.getUserAccount(userId)).thenReturn(Mono.just(usersAccountsEntityDto))
        Mockito.`when`(usersAccountsService.updateUserAccount(MockitoHelper.anyObject(), userId))
            .thenReturn(Mono.just(usersAccountsEntityDto))

        val result = complexUsersService.handleBalanceAction(userId, BigDecimal(200), BalanceActionType.DEPOSIT).block()

        val expectedResult = ComplexUserDto(
            userId = userId,
            registrationDate = registrationDate,
            balanceAmount = BigDecimal(1000),
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            true,
        )

        Assertions.assertEquals(expectedResult, result)
    }

}

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}