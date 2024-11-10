package ru.itmo.common.dto

import java.time.Instant

data class UserDto(
    val userId: Long,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val accountVerified: Boolean,
    val registrationDate: Instant,
    val role: RoleDto,
    val password: String,
)
