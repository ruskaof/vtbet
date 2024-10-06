package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.response.UserResponse
import java.math.BigDecimal
import java.time.Instant

data class UserDto(
    val id: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
)

fun UserDto.toResponse() = UserResponse(
    id = id,
    registrationDate = registrationDate,
    balanceAmount = balanceAmount,
    username = username,
    email = email,
    phoneNumber = phoneNumber,
)

