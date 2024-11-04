package ru.itmo.bets.handler.exception

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class VtbetExceptionResponse(
    val statusCode: Int,
    val message: String,
)