package ru.itmo.user.accounter.service

import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserAccountDto
import ru.itmo.common.dto.UserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.common.response.UserResponse
import ru.itmo.user.accounter.models.entity.UsersAccountsEntity
import ru.itmo.user.accounter.models.entity.UsersEntity

fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        userId = userId!!,
        balanceAmount = balanceAmount,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

fun UsersEntity.toDto() =
    UserDto(
        userId = userId!!,
        username = username,
        roles = emptySet(),
    )

fun UsersEntity.toWithPasswordDto() =
    UserWithPasswordDto(
        userId = userId!!,
        username = username,
        password = password!!,
        roles = emptySet(),
    )


fun ComplexUserDto.toResponse() =
    UserResponse(
        id = userId,
        username = this.username,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
    )


fun UserDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        password = null,
    )

fun UserWithPasswordDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        password = password,
    )

fun ComplexUserDto.toEntity() =
    UsersAccountsEntity(
        userId = userId,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
    )
