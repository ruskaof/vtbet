package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.model.entity.UsersEntity

@Repository
interface UsersRepository : R2dbcRepository<UsersEntity, Long> {
    fun findByUsername(username: String): Mono<UsersEntity>
}
