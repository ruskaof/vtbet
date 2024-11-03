package ru.itmo.user.accounter.service

import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.model.entity.UsersEntity
import ru.itmo.vtbet.model.response.*

fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        accountId = accountId!!,
        userId = usersEntity.userId!!,
        balanceAmount = balanceAmount,
    )

fun UsersAccountsEntity.toComplexDto() =
    ComplexUserDto(
        userId = usersEntity.userId!!,
        accountId = accountId!!,
        registrationDate = usersEntity.registrationDate,
        balanceAmount = balanceAmount,
        username = usersEntity.username,
        email = usersEntity.email,
        phoneNumber = usersEntity.phoneNumber,
        accountVerified = usersEntity.accountVerified,
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
        usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        ),
        balanceAmount = balanceAmount,
    )
