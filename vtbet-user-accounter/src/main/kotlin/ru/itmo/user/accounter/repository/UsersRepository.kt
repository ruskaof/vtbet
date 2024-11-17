package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.models.entity.UsersEntity

@Repository
interface UsersRepository : R2dbcRepository<UsersEntity, Long> {
    fun findByUsername(username: String): Mono<UsersEntity>
    fun findByUserId(userId: Long): Mono<UsersEntity>
    @Query("SELECT nextval('users_user_id_seq')")
    fun getNextUserId(): Mono<Long?>
    fun deleteByUserId(userId: Long): Mono<Void>
}
