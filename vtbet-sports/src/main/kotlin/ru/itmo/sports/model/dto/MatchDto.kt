package ru.itmo.sports.ru.itmo.sports.model.dto

data class MatchDto(
    val matchId: Long,
    val name: String,
    val sport: SportDto,
    val ended: Boolean,
)
