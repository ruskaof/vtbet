package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BetResponse(
    val id: Long,
    val ratio: BigDecimal,
    val amount: BigDecimal,
    val userId: Long,
    val typeOfBetMatch: SimpleTypeOfBetMatchResponse,
)
