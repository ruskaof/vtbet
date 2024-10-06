package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.response.MatchResponse

data class MatchDto(
    val id: Long,
    val name: String,
    val sport: SportDto,
    val ended: Boolean,
)

fun MatchesEntity.toDto() = MatchDto(
    id = matchId!!,
    name = matchName,
    sport = sportEntity.toDto(),
    ended = ended
)

fun MatchDto.toResponse() = MatchResponse(
    id = id,
    name = name,
    sport = sport.toResponse(),
    ended = ended,
)