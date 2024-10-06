package ru.itmo.vtbet.model.response

data class AvailableBetResponse(
    val match: MatchResponse,
    val description: String,
)
