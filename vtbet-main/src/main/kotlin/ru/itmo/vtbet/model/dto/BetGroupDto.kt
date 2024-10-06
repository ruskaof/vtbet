package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.response.BetGroupResponse
import ru.itmo.vtbet.model.response.TypeOfBetResponse

data class BetGroupDto(
    val id: Long,
    val typeOfBets: List<TypeOfBetDto>
)

data class TypeOfBetDto(
    val id: Long,
    val description: String,
)

fun TypeOfBetDto.toResponse(): TypeOfBetResponse = TypeOfBetResponse(
    id = id,
    description = description
)


fun BetGroupDto.toResponse(): BetGroupResponse = BetGroupResponse(
    id = id,
    typeOfBets = typeOfBets.map(TypeOfBetDto::toResponse)
)

fun TypeOfBetEntity.toDto(): TypeOfBetDto = TypeOfBetDto(
    id = typeOfBetId!!,
    description = description,
)
