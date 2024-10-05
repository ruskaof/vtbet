package ru.itmo.vtbet.model.request

import java.math.BigDecimal

data class AddMoneyRequest(
    val amount: BigDecimal,
)