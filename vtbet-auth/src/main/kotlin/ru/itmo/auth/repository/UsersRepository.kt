package ru.itmo.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.itmo.auth.model.entity.UsersEntity

@Repository
interface UsersRepository: JpaRepository<UsersEntity, Long> {
    fun findByUsername(username: String): UsersEntity
}
