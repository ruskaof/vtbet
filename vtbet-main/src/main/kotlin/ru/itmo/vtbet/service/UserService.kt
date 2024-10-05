package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.dto.toDto
import ru.itmo.vtbet.model.dto.toEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.repository.UsersRepository
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val usersRepository: UsersRepository,
) {

     fun getUser(id: Long): UserDto? =
        usersRepository.findById(id).getOrNull()?.toDto()

    fun createUser(): UserDto =
        usersRepository.save(UsersEntity(registrationDate = Instant.now())).toDto()
}