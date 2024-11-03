package ru.itmo.user.accounter.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity

interface UsersAccountsRepository: JpaRepository<UsersAccountsEntity, Long>
