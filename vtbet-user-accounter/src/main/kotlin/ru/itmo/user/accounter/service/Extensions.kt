package ru.itmo.user.accounter.service

import ru.itmo.common.response.UserResponse
import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.model.entity.UsersEntity

fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        accountId = accountId!!,
        balanceAmount = balanceAmount,
    )

fun UsersEntity.toDto() =
    UserDto(
        userId = userId!!,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )


fun ComplexUserDto.toResponse() =
    UserResponse(
        id = userId,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
    )


fun UserDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

fun ComplexUserDto.toEntity() =
    UsersAccountsEntity(
        accountId = accountId,
        balanceAmount = balanceAmount,
        userId = userId
    )
