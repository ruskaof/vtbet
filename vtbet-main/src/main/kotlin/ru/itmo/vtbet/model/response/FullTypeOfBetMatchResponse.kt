package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FullTypeOfBetMatchResponse(
    val id: Long,
    val matchResponse: MatchResponse,
    val typeOfBetResponse: TypeOfBetResponse,
    val ratioNow: BigDecimal,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimpleTypeOfBetMatchResponse(
    val id: Long,
    val matchId: Long,
    val typeOfBetId: Long,
    val typeOfBetDescription: String,
    val ratioNow: BigDecimal,
)
