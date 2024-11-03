package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FullTypeOfBetMatchResponse(
    val id: Long,
    val match: MatchResponse,
    val group: BetGroupResponse,
    val ratio: BigDecimal,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AvailableBetsResponse(
    val id: Long,
    val matchId: Long,
    val groupId: Long,
    val ratio: BigDecimal,
    val betsClosed: Boolean,
)
