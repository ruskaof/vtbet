package ru.itmo.vtbet.model.response

import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.TypeOfBetDto
import java.math.BigDecimal

data class TypeOfBetMatchResponse(
        val id: Long,
        val matchResponse: MatchResponse,
        val typeOfBetResponse: TypeOfBetResponse,
        val ratioNow: BigDecimal,
)
