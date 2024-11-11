package ru.itmo.common.dto

data class UserDto(
    val userId: Long,
    val username: String,
    val roles: Set<String>,
)