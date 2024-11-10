package ru.itmo.common.request

data class UserPasswordRequestDto(
    val username: String,
    val password: String,
)
