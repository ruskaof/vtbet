package ru.itmo.vtbet.model.response

import java.math.BigDecimal

data class FullTypeOfBetMatchResponse(
    val id: Long,
    val matchResponse: MatchResponse,
    val typeOfBetResponse: TypeOfBetResponse,
    val ratioNow: BigDecimal,
)

data class SimpleTypeOfBetMatchResponse(
    val id: Long,
    val matchId: Long,
    val typeOfBetId: Long,
    val typeOfBetDescription: String,
    val ratioNow: BigDecimal,
)
