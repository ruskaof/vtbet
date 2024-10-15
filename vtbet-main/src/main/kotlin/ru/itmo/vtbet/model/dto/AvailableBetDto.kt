package ru.itmo.vtbet.model.dto

import java.math.BigDecimal

data class AvailableBetDto(
    val availableBetId: Long,
    val ratio: BigDecimal,
    val groupId: Long,
    val betsClosed: Boolean,
    val matchId: Long,
)

data class SimpleAvailableBetsDto(
    val availableBetId: Long,
    val betsClosed: Boolean,
    val ratio: BigDecimal,
    val groupId: Long,
    val matchId: Long,
)
