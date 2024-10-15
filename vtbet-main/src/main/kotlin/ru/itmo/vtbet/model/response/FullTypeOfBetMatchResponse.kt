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
data class SimpleAvailableBetsResponse(
    val id: Long,
    val matchId: Long,
    val groupId: Long,
    val ratio: String,
    val betsClosed: Boolean,
)
