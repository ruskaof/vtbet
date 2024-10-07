package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateBetGroupRequest(
    val typeOfBets: List<TypeOfBetRequest>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TypeOfBetRequest(
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val description: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UpdateTypeOfBetMatchRequest(
    @Min(value = 0)
    val ratio: Double,
)
