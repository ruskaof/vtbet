package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FullTypeOfBetMatchResponse(
    val id: Long,
    val match: MatchResponse,
    val group: BetGroupResponse,
    val ratio: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AvailableBetsResponse(
    val id: Long,
    val matchId: Long,
    val groupId: Long,
    val ratio: String,
    //FIXME: мы это поле никак не используем, нужно что-то придумать
    val betsClosed: Boolean,
)
