package ru.itmo.bets.handler.model.dto

import java.math.BigDecimal
import java.time.Instant

data class UserAccountDto(
    val userId: Long,
    var balanceAmount: BigDecimal,
    var email: String?,
    var phoneNumber: String?,
    var accountVerified: Boolean,
    val registrationDate: Instant
)
