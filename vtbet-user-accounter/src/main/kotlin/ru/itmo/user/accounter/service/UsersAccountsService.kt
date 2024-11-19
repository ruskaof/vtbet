package ru.itmo.user.accounter.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.common.utils.scaled
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.repository.UsersAccountsRepository
import java.math.BigDecimal
import java.time.Instant

@Service
class UsersAccountsService(
    private val usersAccountsRepository: UsersAccountsRepository,
) {
    fun getUserAccount(userId: Long): Mono<UserAccountDto> =
        usersAccountsRepository.findById(userId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with id $userId not found")))
            .map { it.toDto() }

    fun createUserAccount(request: CreateUserRequestDto, userId: Long): Mono<UserAccountDto> =
        usersAccountsRepository.save(
            UsersAccountsEntity(
                userId = userId,
                balanceAmount = BigDecimal.ZERO,
                email = request.email,
                phoneNumber = request.phoneNumber,
                accountVerified = true,
                registrationDate = Instant.now(),
            ).apply { this.isUserNew = true }
        ).map { it.toDto() }

    fun updateUserAccount(request: UpdateUserRequestDto, userId: Long): Mono<UserAccountDto> =
        usersAccountsRepository.findById(userId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with id $userId not found")))
            .flatMap {
                usersAccountsRepository.save(
                    it.copy(
                        email = request.email ?: it.email,
                        phoneNumber = request.phoneNumber ?: it.phoneNumber,
                    )
                )
            }.map { it.toDto() }

    fun delete(userId: Long): Mono<Void> =
        usersAccountsRepository.deleteById(userId)

    @Transactional
    fun handleBalanceAction(userId: Long, amount: BigDecimal, action: BalanceActionType): Mono<UserAccountDto> =
        getUserAccount(userId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with id $userId not found")))
            .flatMap { userAccount ->
                val updatedAccount = when (action) {
                    BalanceActionType.DEPOSIT -> userAccount.copy(
                        balanceAmount = userAccount.balanceAmount.add(amount).scaled(),
                    )

                    BalanceActionType.WITHDRAW -> {
                        if (userAccount.balanceAmount < amount) {
                            return@flatMap Mono.error(IllegalBetActionException("User does not have enough money to withdraw"))
                        }
                        userAccount.copy(
                            balanceAmount = userAccount.balanceAmount.subtract(amount).scaled(),
                        )
                    }
                }
                usersAccountsRepository.updateBalanceById(userId, updatedAccount.balanceAmount.toDouble())
                    .thenReturn(updatedAccount)
            }
}