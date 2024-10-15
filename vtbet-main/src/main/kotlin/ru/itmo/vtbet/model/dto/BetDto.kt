package ru.itmo.vtbet.model.dto

import java.math.BigDecimal

data class BetDto(
    val betId: Long,
    val ratio: BigDecimal,
    val amount: BigDecimal,
    val userId: Long,
    val availableBetId: Long,
)
