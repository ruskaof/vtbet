package ru.itmo.user.accounter.service

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserAccountDto
import ru.itmo.user.accounter.repository.UsersAccountsRepository

@Service
class UsersAccountsService(
    private val usersAccountsRepository: UsersAccountsRepository,
    private val databaseClient: DatabaseClient,
) {
    fun getUserAccount(userId: Long): Mono<UserAccountDto> =
        usersAccountsRepository.findByUserId(userId).map { it.toDto() }

    fun save(userAccountDto: ComplexUserDto): Mono<UserAccountDto> =
        usersAccountsRepository.save(
            userAccountDto
                .toEntity()
        ).map { it.toDto() }

    fun update(userAccountDto: ComplexUserDto) =
        usersAccountsRepository.save(
            userAccountDto.toEntity()
        ).map { it.toDto() }

    fun updateBalanceById(userId: Long, balance: Double) =
        usersAccountsRepository.updateBalanceById(userId, balance)

    fun deleteByUserId(userId: Long): Mono<Long> =
        databaseClient.sql("DELETE FROM users_accounts WHERE user_id = :userId")
            .bind("userId", userId)
            .fetch()
            .rowsUpdated();
}