package ru.itmo.vtbet.model.request

data class CreateBetGroupRequest(
    val typeOfBets: List<TypeOfBetDto>
)

data class TypeOfBetDto(
    val description: String
)