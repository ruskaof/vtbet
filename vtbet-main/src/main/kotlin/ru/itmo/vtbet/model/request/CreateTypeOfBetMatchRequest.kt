package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateTypeOfBetMatchRequest(
    val typeOfBetId: Long,
    @field:PositiveOrZero
    val ratioNow: BigDecimal,
)
