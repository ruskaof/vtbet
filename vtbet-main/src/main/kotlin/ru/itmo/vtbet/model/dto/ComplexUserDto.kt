package ru.itmo.vtbet.model.dto

import java.math.BigDecimal
import java.time.Instant

data class ComplexUserDto(
    val userId: Long,
    val accountId: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val accountVerified: Boolean,
)
