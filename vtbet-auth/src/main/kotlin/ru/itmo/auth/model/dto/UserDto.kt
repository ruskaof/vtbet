package ru.itmo.auth.model.dto

class UserDto (
    val userId: Long,
    val username: String,
    val roles: Set<String>,
)
