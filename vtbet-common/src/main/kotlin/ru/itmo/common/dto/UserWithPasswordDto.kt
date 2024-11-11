package ru.itmo.common.dto

data class UserWithPasswordDto(
    val userId: Long,
    val username: String,
    val roles: Set<String>,
    val password: String,
)
