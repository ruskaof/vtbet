package ru.itmo.user.accounter.service

import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserAccountDto
import ru.itmo.common.dto.UserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.common.entity.RolesEntity
import ru.itmo.common.entity.UsersAccountsEntity
import ru.itmo.common.entity.UsersEntity
import ru.itmo.common.response.UserResponse

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
        roles = roles.map { it.name }.toSet(),
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
        roles = roles.map { RolesEntity(name = it) }.toSet(),
        password = null,
    )

fun UserWithPasswordDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        roles = roles.map { RolesEntity(name = it) }.toSet(),
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
