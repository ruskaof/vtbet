package ru.itmo.user.accounter.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.common.utils.scaled
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.repository.UsersAccountsRepository
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.toDto
import ru.itmo.user.accounter.service.toResponse
import java.math.BigDecimal
import java.time.Instant

class UsersAccountsServiceTest {
    private val usersAccountsRepository = Mockito.mock(UsersAccountsRepository::class.java)
    private val usersAccountsService = UsersAccountsService(usersAccountsRepository)


    @Test
    fun `get user account`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))

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

    @Test
    fun createUserAccount() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val createUserRequestDto = CreateUserRequestDto(
            email = email,
            phoneNumber = phoneNumber,
        )

        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))

        val result = usersAccountsService.createUserAccount(createUserRequestDto, userId).block()

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

    @Test
    fun updateUserAccount() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val updateUserRequestDto = UpdateUserRequestDto(
            email = email,
            phoneNumber = phoneNumber,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        Mockito.`when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))

        val result = usersAccountsService.updateUserAccount(updateUserRequestDto, userId).block()

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

    @Test
    fun `handle balance deposit action`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val deposit = BigDecimal(200)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val usersAccountDto = usersAccountsEntity.toDto()


        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        Mockito.`when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.plus(deposit).toDouble()))
            .thenReturn(Mono.just(userId))

        val result = usersAccountsService.handleBalanceAction(userId, deposit, BalanceActionType.DEPOSIT).block()

        val expectedResult = usersAccountDto.copy(
            balanceAmount = balanceAmount.plus(deposit).scaled(),
        )

        Assertions.assertNotNull(result)
        Assertions.assertEquals(expectedResult, result)
        Assertions.assertEquals(expectedResult.toResponse().id, result!!.toResponse().id)
    }

    @Test
    fun `handle balance withdraw action`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(1000)
        val deposit = BigDecimal(200)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val usersAccountDto = UserAccountDto(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )


        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        Mockito.`when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.minus(deposit).toDouble()))
            .thenReturn(Mono.just(userId))

        val result = usersAccountsService.handleBalanceAction(userId, deposit, BalanceActionType.WITHDRAW).block()

        val expectedResult = usersAccountDto.copy(
            balanceAmount = balanceAmount.minus(deposit).scaled(),
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `handle balance withdraw action exception`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(200)
        val deposit = BigDecimal(1000)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        Mockito.`when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        Mockito.`when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.minus(deposit).toDouble()))
            .thenReturn(Mono.just(userId))

        val result = usersAccountsService.handleBalanceAction(userId, deposit, BalanceActionType.WITHDRAW)

        StepVerifier.create(result)
            .expectErrorMatches { ex ->
                ex is IllegalBetActionException &&
                        ex.message == "User does not have enough money to withdraw"
            }
            .verify()
    }

    @Test
    fun `delete by id`() {
        val userId = 1L

        Mockito.`when`(usersAccountsRepository.deleteById(userId)).thenReturn(Mono.empty())

        usersAccountsService.delete(userId).block()

        Mockito.verify(usersAccountsRepository).deleteById(userId)
    }
}
