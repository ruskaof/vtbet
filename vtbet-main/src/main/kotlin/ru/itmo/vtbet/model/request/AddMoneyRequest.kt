package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AddMoneyRequest(
    @field:Positive(message = "Amount must be positive")
    val amount: BigDecimal,
)