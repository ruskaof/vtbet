package ru.itmo.vtbet.service

import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.TypeOfBetDto
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.response.BetGroupResponse
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SimpleTypeOfBetMatchResponse
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.model.response.TypeOfBetResponse
import ru.itmo.vtbet.model.response.UserResponse


fun BetsEntity.toDto() =
    BetDto(
        id = this.id!!,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.usersEntity.id!!,
        availableBetId = this.availableBetId,
    )

fun TypeOfBetDto.toResponse(): TypeOfBetResponse = TypeOfBetResponse(
    id = id,
    description = description
)

fun BetGroupDto.toResponse(): BetGroupResponse = BetGroupResponse(
    id = id,
    typeOfBets = typeOfBets.map(TypeOfBetDto::toResponse)
)

fun TypeOfBetEntity.toDto(): TypeOfBetDto = TypeOfBetDto(
    id = id!!,
    description = description,
)

fun MatchesEntity.toDto() = MatchDto(
    id = matchId!!,
    name = matchName,
    sport = sport.toDto(),
    ended = ended
)

fun MatchDto.toResponse() = MatchResponse(
    id = id,
    name = name,
    sport = sport.toResponse(),
    ended = ended,
)

fun SportEntity.toDto(): SportDto =
    SportDto(sportId!!, sportName)

fun SportDto.toResponse(): SportResponse =
    SportResponse(id, name)

fun SimpleTypeOfBetMatchDto.toResponse() =
    SimpleTypeOfBetMatchResponse(
        id = this.id,
        ratioNow = this.ratioNow,
        matchId = this.matchId,
        typeOfBetDescription = this.typeOfBet.description,
        typeOfBetId = this.typeOfBet.id,
    )

fun UserDto.toResponse() = UserResponse(
    id = id,
    registrationDate = registrationDate,
    balanceAmount = balanceAmount,
    username = username,
    email = email,
    phoneNumber = phoneNumber,
)

fun SportDto.toEntity() =
    SportEntity(id, name)

fun MatchDto.toEntity() =
    MatchesEntity(
        matchId = id,
        matchName = name,
        sport = sport.toEntity(),
        ended = ended,
    )

fun UserDto.toUsersEntity() =
    UsersEntity(
        id = id,
        registrationDate = registrationDate,
    )

fun AvailableBetEntity.toDto() =
    AvailableBetDto(id!!, ratioNow, typeOfBetId, betsClosed, matchId)