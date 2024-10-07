package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.TypeOfBetMatchResponse
import java.math.BigDecimal

data class TypeOfBetMatchDto(
        val id: Long,
        val ratioNow: BigDecimal,
        val typeOfBet: TypeOfBetDto,
        val match: MatchDto,
)

fun TypeOfBetMatchDto.toResponse() = TypeOfBetMatchResponse(
        id = id,
        ratioNow = ratioNow,
        matchResponse = match.toResponse(),
        typeOfBetResponse = typeOfBet.toResponse(),
)

fun TypeOfBetMatchEntity.toDto() = TypeOfBetMatchDto(
        id = id!!,
        ratioNow = ratioNow,
        match = matchesEntity.toDto(),
        typeOfBet = typeOfBetEntity.toDto(),
)