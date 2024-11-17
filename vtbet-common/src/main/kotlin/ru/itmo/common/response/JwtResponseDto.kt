package ru.itmo.common.response

data class JwtResponseDto(
    val userId: Long,
    val jwt: String,
)
