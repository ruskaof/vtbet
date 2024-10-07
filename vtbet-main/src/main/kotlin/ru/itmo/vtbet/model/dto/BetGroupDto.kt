package ru.itmo.vtbet.model.dto

data class BetGroupDto(
    val id: Long,
    val typeOfBets: List<TypeOfBetDto>
)

data class TypeOfBetDto(
    val id: Long,
    val description: String,
)
