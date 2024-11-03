package ru.itmo.user.accounter.model.dto

import java.math.BigDecimal

data class UserAccountDto(
    val accountId: Long,
    val balanceAmount: BigDecimal,
)