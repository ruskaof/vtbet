package ru.itmo.vtbet.model.response

import java.time.Instant

data class UserResponse(
    val id: Long,
    val registrationDate: Instant,
)
