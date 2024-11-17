package ru.itmo.user.accounter.service

import ru.itmo.common.response.UserResponse
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity

fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        userId = userId,
        balanceAmount = balanceAmount,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

fun UserAccountDto.toEntity() =
    UsersAccountsEntity(
        userId = userId,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
    )

fun UserAccountDto.toResponse() =
    UserResponse(
        id = userId,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount
    )

