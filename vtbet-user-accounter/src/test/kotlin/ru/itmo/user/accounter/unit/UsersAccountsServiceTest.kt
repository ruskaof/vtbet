package ru.itmo.user.accounter.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
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
    private val usersAccountsRepository = mock(UsersAccountsRepository::class.java)
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

        `when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))

        val result = usersAccountsService.getUserAccount(userId).block()

        val expectedResult = UserAccountDto(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        assertEquals(expectedResult, result)
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

        `when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))

        val result = usersAccountsService.createUserAccount(createUserRequestDto, userId).block()

        val expectedResult = UserAccountDto(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        assertEquals(expectedResult, result)
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

        `when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        `when`(usersAccountsRepository.save(any())).thenReturn(Mono.just(usersAccountsEntity))

        val result = usersAccountsService.updateUserAccount(updateUserRequestDto, userId).block()

        val expectedResult = UserAccountDto(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        assertEquals(expectedResult, result)
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


        `when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        `when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.plus(deposit).toDouble()))
            .thenReturn(Mono.just(userId))

        val result = usersAccountsService.handleBalanceAction(userId, deposit, BalanceActionType.DEPOSIT).block()

        val expectedResult = usersAccountDto.copy(
            balanceAmount = balanceAmount.plus(deposit).scaled(),
        )

        assertNotNull(result)
        assertEquals(expectedResult, result)
        assertEquals(expectedResult.toResponse().id, result!!.toResponse().id)
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


        `when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        `when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.minus(deposit).toDouble()))
            .thenReturn(Mono.just(userId))

        val result = usersAccountsService.handleBalanceAction(userId, deposit, BalanceActionType.WITHDRAW).block()

        val expectedResult = usersAccountDto.copy(
            balanceAmount = balanceAmount.minus(deposit).scaled(),
        )
        assertEquals(expectedResult, result)
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

        `when`(usersAccountsRepository.findById(userId)).thenReturn(Mono.just(usersAccountsEntity))
        `when`(usersAccountsRepository.updateBalanceById(userId, balanceAmount.minus(deposit).toDouble()))
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

        `when`(usersAccountsRepository.deleteById(userId)).thenReturn(Mono.empty())

        usersAccountsService.delete(userId).block()

        verify(usersAccountsRepository).deleteById(userId)
    }

    @Test
    fun `getNotVerifiedUsers returns PagingDto with users`() {
        val userId1 = 1L
        val userId2 = 2L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(200)

        val usersAccountsEntity1 = UsersAccountsEntity(
            userId = userId1,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val usersAccountsEntity2 = UsersAccountsEntity(
            userId = userId2,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        val userEntities = listOf(usersAccountsEntity1, usersAccountsEntity2)
        val pageSize = 2
        val pageNumber = 0

        `when`(usersAccountsRepository.findAllByAccountVerifiedFalse(pageSize, pageNumber * pageSize))
            .thenReturn(Flux.fromIterable(userEntities))
        `when`(usersAccountsRepository.countAllByAccountVerifiedFalse())
            .thenReturn(Mono.just(2L))

        val result = usersAccountsService.getNotVerifiedUsers(pageSize, pageNumber)

        StepVerifier.create(result)
            .assertNext { pagingDto ->
                assertEquals(2, pagingDto.items.size)
                assertEquals(2, pagingDto.total)
                assertEquals(pageNumber, pagingDto.page)
                assertEquals(pageSize, pagingDto.pageSize)
                assertEquals(userId1, pagingDto.items[0].userId)
                assertEquals(userId2, pagingDto.items[1].userId)
            }
            .verifyComplete()

        verify(usersAccountsRepository).findAllByAccountVerifiedFalse(pageSize, pageNumber * pageSize)
        verify(usersAccountsRepository).countAllByAccountVerifiedFalse()
    }

    @Test
    fun `getNotVerifiedUsers throws ResourceNotFoundException when no users found`() {
        `when`(usersAccountsRepository.findAllByAccountVerifiedFalse(anyInt(), anyInt()))
            .thenReturn(Flux.empty())
        `when`(usersAccountsRepository.countAllByAccountVerifiedFalse())
            .thenReturn(Mono.just(0L))

        val result = usersAccountsService.getNotVerifiedUsers(2, 0)

        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is ResourceNotFoundException &&
                        throwable.message == "There are no users"
            }.verify()

        verify(usersAccountsRepository).findAllByAccountVerifiedFalse(anyInt(), anyInt())
    }
}
