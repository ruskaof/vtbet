package ru.itmo.common.dto

import java.math.BigDecimal

data class BetCreatedEventDto(
    val betId: Long,
    val userId: Long,
    val betAmount: BigDecimal,
    val matchId: Long,
    val matchName: String,
)
