package ru.itmo.vtbet.model.request

import jakarta.validation.constraints.Size

data class CreateMatchRequest(
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val name: String,
)
