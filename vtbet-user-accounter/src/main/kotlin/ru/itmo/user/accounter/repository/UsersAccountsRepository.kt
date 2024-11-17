package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity

@Repository
interface UsersAccountsRepository: R2dbcRepository<UsersAccountsEntity, Long> {
    @Query("UPDATE users_accounts SET balance_amount = :balanceAmount WHERE user_id = :userId")
    fun updateBalanceById(userId: Long, balanceAmount: Double): Mono<Long>
}
