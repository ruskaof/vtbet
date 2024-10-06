package ru.itmo.vtbet.model.response

data class BetGroupResponse(
    val id: Long,
    val typeOfBets: List<TypeOfBetResponse>
)

data class TypeOfBetResponse(
    val id: Long,
    val description: String,
)
