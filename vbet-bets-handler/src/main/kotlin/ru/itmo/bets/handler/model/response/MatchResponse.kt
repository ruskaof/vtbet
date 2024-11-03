package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import ru.itmo.bets.handler.model.response.SportResponse

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MatchResponse(
    val id: Long,
    val name: String,
    val sport: SportResponse,
    val ended: Boolean,
)