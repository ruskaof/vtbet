package ru.itmo.vtbet.model.response

data class MatchResponse(
    val id: Long,
    val name: String,
    val sport: SportResponse,
)