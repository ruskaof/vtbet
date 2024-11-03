package ru.itmo.user.accounter.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.itmo.common.exception.DuplicateException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.utils.scaled
import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.user.accounter.model.entity.UsersEntity
import java.math.BigDecimal

import java.time.Instant
import java.time.ZoneOffset

@Service
class ComplexUsersService(
    private val usersOperationsService: UsersOperationsService,
    private val usersAccountsService: UsersAccountsService,
    @Value("\${vtbet.ratio-decrease-value}")
    private val ratioDecreaseValue: BigDecimal,
) {
    // fixme exception handling?
    @Transactional
    fun getUser(userId: Long): Mono<ComplexUserDto> =
        Mono.zip(
            usersOperationsService.getUser(userId),
            usersAccountsService.getUserAccount(userId)
        ).map {
            ComplexUserDto(
                userId = it.t1.userId,
                accountId = it.t2.accountId,
                registrationDate = it.t1.registrationDate.atOffset(ZoneOffset.ofHours(3)).toInstant(),
                balanceAmount = it.t2.balanceAmount,
                username = it.t1.username,
                email = it.t1.email,
                phoneNumber = it.t1.phoneNumber,
                accountVerified = it.t1.accountVerified,
            )
        }

    @Transactional
    fun createUser(
        request: CreateUserRequestDto,
    ): Mono<ComplexUserDto> =// fixme check that username is unique
        usersOperationsService.save(
            UserDto(
                userId = 0,
                username = request.username,
                email = request.email,
                phoneNumber = request.phoneNumber,
                accountVerified = true,
                registrationDate = Instant.now(),
            )
        )
            .flatMap { user ->
                Mono.zip(
                    usersAccountsService.save(
                        ComplexUserDto(
                            accountId = 0,
                            userId = user.userId,
                            balanceAmount = BigDecimal.ZERO,
                            username = request.username,
                            email = request.email,
                            phoneNumber = request.phoneNumber,
                            accountVerified = true,
                            registrationDate = user.registrationDate,
                        )
                    ),
                    Mono.just(user)
                )
            }
            .map {
                ComplexUserDto(it.t2, it.t1)
            }

    @Transactional
    fun deleteUser(userId: Long) =
        usersOperationsService.getUser(userId)
            .map { usersOperationsService.deleteById(userId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with userId $userId not found")))

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequestDto) =
        Mono.zip(
            usersOperationsService.getUser(userId),
            usersAccountsService.getUserAccount(userId),
        )
            .flatMap {
                usersOperationsService.update(UserDto(
                    userId = it.t1.userId,
                    username = request.username ?: it.t1.username,
                    email = request.email ?: it.t1.email,
                    phoneNumber = request.phoneNumber ?: it.t1.phoneNumber,
                    accountVerified = it.t1.accountVerified,
                    registrationDate = it.t1.registrationDate,
                ))
            }
            .flatMap {
                Mono.zip(
                    usersAccountsService.getUserAccount(userId),
                    Mono.just(it)
                )
            }
            .map { ComplexUserDto(it.t2, it.t1) }

    @Transactional
    fun handleBalanceAction(userId: Long, amount: BigDecimal, action: BalanceActionType) =
        getUser(userId)
            .flatMap { userAccount ->
                val updatedAccount = when (action) {
                    BalanceActionType.DEPOSIT -> userAccount.copy(
                        balanceAmount = userAccount.balanceAmount.add(amount).scaled(),
                    )

                    BalanceActionType.WITHDRAW -> userAccount.copy(
                        balanceAmount = userAccount.balanceAmount.subtract(amount).scaled(),
                    )
                }
                usersAccountsService.update(
                   updatedAccount
                )
            }
            .flatMap { getUser(userId) }
}