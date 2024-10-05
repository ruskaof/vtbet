package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.response.UserResponse
import java.time.Instant

data class UserDto(
    val id: Long,
    val registrationDate: Instant,
)

fun UserDto.toEntity() = UsersEntity(
    id = id,
    registrationDate = registrationDate,
)

fun UsersEntity.toDto() = UserDto(
    id = id!!,
    registrationDate = registrationDate,
)

fun UserDto.toResponse() = UserResponse(
    id = id,
    registrationDate = registrationDate,
)
