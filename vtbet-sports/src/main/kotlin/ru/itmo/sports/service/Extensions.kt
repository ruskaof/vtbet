package ru.itmo.sports.ru.itmo.sports.service

import ru.itmo.common.response.MatchResponse
import ru.itmo.common.response.SportResponse
import ru.itmo.sports.ru.itmo.sports.model.dto.MatchDto
import ru.itmo.sports.ru.itmo.sports.model.dto.SportDto
import ru.itmo.sports.ru.itmo.sports.model.entity.MatchesEntity
import ru.itmo.sports.ru.itmo.sports.model.entity.SportsEntity


fun MatchesEntity.toDto() = MatchDto(
    matchId = matchId!!,
    name = matchName,
    sport = sport.toDto(),
    ended = ended
)

fun MatchDto.toResponse() = MatchResponse(
    id = matchId,
    name = name,
    sport = sport.toResponse(),
    ended = ended,
)

fun SportsEntity.toDto(): SportDto =
    SportDto(sportId!!, sportName)

fun SportDto.toResponse(): SportResponse =
    SportResponse(
        id = sportId,
        name = name,
    )

fun SportDto.toEntity() =
    SportsEntity(sportId, name)

fun MatchDto.toEntity() =
    MatchesEntity(
        matchId = matchId,
        matchName = name,
        sport = sport.toEntity(),
        ended = ended,
    )
