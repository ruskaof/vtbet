package ru.itmo.vtbet.service

import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.TypeOfBetDto
import ru.itmo.vtbet.model.dto.TypeOfBetMatchDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.response.BetGroupResponse
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SimpleTypeOfBetMatchResponse
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.model.response.TypeOfBetResponse
import ru.itmo.vtbet.model.response.UserResponse

fun TypeOfBetMatchEntity.toResponse() =
    SimpleTypeOfBetMatchResponse(
        id = this.id!!,
        matchId = this.match.matchId!!,
        typeOfBetId = this.typeOfBets.typeOfBetId!!,
        typeOfBetDescription = this.typeOfBets.description,
        ratioNow = this.ratioNow,
    )

fun BetsEntity.toDto() =
    BetDto(
        id = this.id!!,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.usersEntity.id!!,
        typeOfBetMatch = this.typeOfBetMatch.toSimpleDto(),
    )

fun BetDto.toResponse() =
    BetResponse(
        id = this.id,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.userId,
        typeOfBetMatch = this.typeOfBetMatch.toResponse(),
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
    id = typeOfBetId!!,
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

fun TypeOfBetMatchDto.toResponse() =
    FullTypeOfBetMatchResponse(
        id = id,
        ratioNow = ratioNow,
        matchResponse = match.toResponse(),
        typeOfBetResponse = typeOfBet.toResponse(),
    )

fun TypeOfBetMatchEntity.toSimpleDto() =
    SimpleTypeOfBetMatchDto(
        id = this.id!!,
        ratioNow = this.ratioNow,
        matchId = this.match.matchId!!,
        typeOfBet = this.typeOfBets.toDto(),
    )

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
