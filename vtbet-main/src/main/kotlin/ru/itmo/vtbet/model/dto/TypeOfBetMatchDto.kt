package ru.itmo.vtbet.model.dto

import java.math.BigDecimal

data class TypeOfBetMatchDto(
    val id: Long,
    val ratioNow: BigDecimal,
    val typeOfBet: TypeOfBetDto,
    val match: MatchDto,
)

data class SimpleTypeOfBetMatchDto(
    val id: Long,
    val ratioNow: BigDecimal,
    val typeOfBet: TypeOfBetDto,
    val matchId: Long,
)
