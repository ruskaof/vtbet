package ru.itmo.bets.handler.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateAvailableBetRequestDto(
    val groupId: Long,
    @field:PositiveOrZero
    val ratio: BigDecimal,
)
