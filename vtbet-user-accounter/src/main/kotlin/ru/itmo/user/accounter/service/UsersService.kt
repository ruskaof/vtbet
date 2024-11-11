package ru.itmo.user.accounter.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.common.exception.DuplicateException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.common.utils.scaled
import java.math.BigDecimal
import java.sql.Timestamp
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
                registrationDate = it.t2.registrationDate.atOffset(ZoneOffset.ofHours(3)).toInstant(),
                balanceAmount = it.t2.balanceAmount,
                username = it.t1.username,
                email = it.t2.email,
                phoneNumber = it.t2.phoneNumber,
                accountVerified = it.t2.accountVerified,
            )
        }

    @Transactional
    fun createUser(
        request: CreateUserRequestDto,
    ): Mono<ComplexUserDto> =
        usersOperationsService.getByUserName(request.username)
            .flatMap { Mono.error<ComplexUserDto>(DuplicateException("User with username ${request.username} already exists")) }
            .switchIfEmpty(
                usersOperationsService.save(
                    UserWithPasswordDto(
                        userId = 0,
                        username = request.username,
                        password = request.password,
                        roles = setOf("USER")
                    )
                ).flatMap { user ->
                    Mono.zip(
                        Mono.just(user),
                        usersAccountsService.save(
                            ComplexUserDto(
                                userId = user.userId,
                                balanceAmount = BigDecimal.ZERO,
                                username = request.username,
                                email = request.email,
                                phoneNumber = request.phoneNumber,
                                accountVerified = true,
                                registrationDate = Timestamp(Instant.now().toEpochMilli()).toInstant(),
                            )
                        ),
                    )
                }.map {
                    ComplexUserDto(it.t1, it.t2)
                }
            )


    @Transactional
    fun deleteUser(userId: Long) =
        usersOperationsService.getUser(userId)
            .map { usersOperationsService.deleteById(userId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with userId $userId not found")))

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequestDto): Mono<ComplexUserDto> =
        Mono.zip(
            usersOperationsService.getUser(userId),
            usersAccountsService.getUserAccount(userId),
        ).flatMap {
            usersAccountsService.update(
                ComplexUserDto(
                    userId = it.t1.userId,
                    registrationDate = it.t2.registrationDate,
                    balanceAmount = it.t2.balanceAmount,
                    username = it.t1.username,
                    email = request.email ?: it.t2.email,
                    phoneNumber = request.phoneNumber ?: it.t2.phoneNumber,
                    accountVerified = request.isVerified ?: it.t2.accountVerified,
                )
            )
        }.flatMap {
            Mono.zip(
                usersOperationsService.getUser(userId),
                usersAccountsService.getUserAccount(userId),
            )
        }.map { ComplexUserDto(it.t1, it.t2) }

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