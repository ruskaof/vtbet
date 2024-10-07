package ru.itmo.vtbet.model.dto

data class MatchDto(
    val id: Long,
    val name: String,
    val sport: SportDto,
    val ended: Boolean,
)
