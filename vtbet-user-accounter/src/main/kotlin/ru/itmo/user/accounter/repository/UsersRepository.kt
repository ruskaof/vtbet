package ru.itmo.user.accounter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.itmo.user.accounter.model.entity.UsersEntity
import java.util.*

@Repository
interface UsersRepository : JpaRepository<UsersEntity, Long> {
    fun findByUsername(username: String): Optional<UsersEntity>
}
