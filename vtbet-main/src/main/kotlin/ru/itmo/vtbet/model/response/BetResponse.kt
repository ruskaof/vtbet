package ru.itmo.vtbet.model.response

import java.math.BigDecimal

data class BetResponse(
    val id: Long,
    val ratio: BigDecimal,
    val amount: BigDecimal,
    val userId: Long,
    val typeOfBetMatch: SimpleTypeOfBetMatchResponse,
)
