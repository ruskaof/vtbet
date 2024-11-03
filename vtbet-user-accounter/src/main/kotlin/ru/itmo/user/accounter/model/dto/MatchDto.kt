package ru.itmo.user.accounter.model.dto

import ru.itmo.vtbet.model.dto.SportDto

data class MatchDto(
    val matchId: Long,
    val name: String,
    val sport: SportDto,
    val ended: Boolean,
)
