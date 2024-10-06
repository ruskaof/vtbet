package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.response.MatchResponse

data class MatchDto(
    val id: Long,
    val name: String,
    val sport: SportDto,
)

fun MatchesEntity.toDto() = MatchDto(
    id = matchId!!,
    name = matchName,
    sport = sportEntity.toDto(),
)

fun MatchDto.toResponse() = MatchResponse(
    id = id,
    name = name,
    sport = sport.toResponse()
)