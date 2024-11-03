package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity

@Repository
interface UsersAccountsRepository: R2dbcRepository<UsersAccountsEntity, Long>
