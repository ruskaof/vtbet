package ru.itmo.vtbet.model.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class CreateBetGroupRequest(
    val typeOfBets: List<TypeOfBetRequest>
)

data class TypeOfBetRequest(
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val description: String
)

data class UpdateTypeOfBetMatchRequest(
    @Min(value = 0)
    val ratio: Double,
)
