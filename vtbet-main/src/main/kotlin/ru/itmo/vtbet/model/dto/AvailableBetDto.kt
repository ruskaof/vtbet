package ru.itmo.vtbet.model.dto

import java.math.BigDecimal

data class AvailableBetDto(
    val id: Long,
    val ratioNow: BigDecimal,
    val typeOfBetId: Long,
    val betsClosed: Boolean,
    val matchId: Long,
)

data class SimpleTypeOfBetMatchDto(
    val id: Long,
    val ratioNow: BigDecimal,
    val typeOfBet: TypeOfBetDto,
    val matchId: Long,
)
