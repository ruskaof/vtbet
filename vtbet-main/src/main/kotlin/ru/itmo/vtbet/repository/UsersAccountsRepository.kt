package ru.itmo.vtbet.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.UsersAccountsEntity

interface UsersAccountsRepository: JpaRepository<UsersAccountsEntity, Long>
