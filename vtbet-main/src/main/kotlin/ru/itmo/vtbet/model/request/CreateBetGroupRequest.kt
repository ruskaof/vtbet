package ru.itmo.vtbet.model.request

import jakarta.validation.constraints.Size

data class CreateBetGroupRequest(
    val typeOfBets: List<TypeOfBetDto>
)

data class TypeOfBetDto(
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val description: String
)