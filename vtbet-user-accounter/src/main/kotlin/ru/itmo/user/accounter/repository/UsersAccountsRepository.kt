package ru.itmo.user.accounter.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import ru.itmo.common.entity.UsersAccountsEntity

@Repository
interface UsersAccountsRepository: R2dbcRepository<UsersAccountsEntity, Long>
