package ru.itmo.vtbet.model.request

import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class CreateTypeOfBetMatchRequest(
    val typeOfBetId: Long,
    @field:PositiveOrZero
    val ratioNow: BigDecimal,
)
