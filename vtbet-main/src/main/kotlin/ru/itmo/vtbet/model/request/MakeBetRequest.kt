package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Min
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MakeBetRequest(
    val userId: Long,
    @field:Min(value = 0L)
    val ratio: BigDecimal,
    @field:Min(value = 0L)
    val amount: BigDecimal,
)
