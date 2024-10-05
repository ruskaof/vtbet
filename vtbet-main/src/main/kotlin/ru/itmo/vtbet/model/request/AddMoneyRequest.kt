package ru.itmo.vtbet.model.request

import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class AddMoneyRequest(
    @field:Positive(message = "Amount must be positive")
    val amount: BigDecimal,
)