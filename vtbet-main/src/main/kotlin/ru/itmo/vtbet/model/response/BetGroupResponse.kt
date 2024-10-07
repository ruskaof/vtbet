package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BetGroupResponse(
    val id: Long,
    val typeOfBets: List<TypeOfBetResponse>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TypeOfBetResponse(
    val id: Long,
    val description: String,
)
