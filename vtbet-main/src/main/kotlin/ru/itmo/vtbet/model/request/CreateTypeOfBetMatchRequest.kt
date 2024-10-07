package ru.itmo.vtbet.model.request

import java.math.BigDecimal

data class CreateTypeOfBetMatchRequest(
        val matchId : Long,
        val typeOfBetId : Long,
        val ratioNow : BigDecimal,
)
