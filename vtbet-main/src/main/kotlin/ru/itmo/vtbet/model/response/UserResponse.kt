package ru.itmo.vtbet.model.response

import java.math.BigDecimal
import java.time.Instant

data class UserResponse(
    val id: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
)
