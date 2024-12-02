package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity

@Repository
interface UsersAccountsRepository: R2dbcRepository<UsersAccountsEntity, Long> {
    @Query("UPDATE users_accounts SET balance_amount = :balanceAmount WHERE user_id = :userId")
    fun updateBalanceById(userId: Long, balanceAmount: Double): Mono<Long>

    @Query("SELECT * FROM users_accounts WHERE account_verified = false LIMIT :limit OFFSET :offset")
    fun findAllByAccountVerifiedFalse(limit: Int, offset: Int): Flux<UsersAccountsEntity>

    @Query("SELECT COUNT(*) FROM users_accounts WHERE account_verified = false")
    fun countAllByAccountVerifiedFalse(): Mono<Long>
}
