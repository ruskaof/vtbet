package ru.itmo.common.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MatchResponse(
    val id: Long,
    val name: String,
    val sport: SportResponse,
    val ended: Boolean,
)