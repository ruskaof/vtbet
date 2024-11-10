package ru.itmo.bets.handler.service

import ru.itmo.bets.handler.entity.AvailableBetsEntity
import ru.itmo.bets.handler.entity.BetsEntity
import ru.itmo.bets.handler.entity.BetsGroupsEntity
import ru.itmo.bets.handler.entity.UsersEntity
import ru.itmo.common.dto.*
import ru.itmo.common.response.*

fun BetsEntity.toDto() =
    BetDto(
        betId = this.betId!!,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.usersEntity.userId!!,
        availableBetId = this.availableBetId,
    )

fun UserDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

//fun BetDto.toResponse() =
//    BetResponse(
//        id = betId,
//        ratio = ratio,
//        amount = amount,
//        userId = userId,
//        availableBetId = availableBetId,
//    )

fun UserResponse.toDto() =
    UserDto(
        userId = this.id,
        username = this.username,
        email = this.email,
        phoneNumber = this.phoneNumber,
        accountVerified = this.accountVerified,
        registrationDate = this.registrationDate,
        role = this.role.toDto(),
        password = this.password,
    )

fun RoleResponse.toDto() =
    RoleDto(
        id = this.id,
        name = this.name,
    )


fun AvailableBetDto.toResponse() =
    AvailableBetsResponse(
        id = availableBetId,
        matchId = matchId,
        groupId = groupId,
        ratio = ratio,
        betsClosed = betsClosed,
    )

fun AvailableBetsEntity.toDto() =
    AvailableBetDto(
        availableBetId = availableBetId!!,
        ratio = ratio,
        groupId = betsGroupsEntity.groupId!!,
        betsClosed = betsClosed,
        matchId = matchId,
    )

fun AvailableBetWithBetGroupDto.toEntity() =
    AvailableBetsEntity(
        availableBetId = availableBetId,
        ratio = ratio,
        betsGroupsEntity = betGroupDto.toEntity(),
        matchId = matchId,
        betsClosed = betsClosed,
    )

fun BetGroupDto.toEntity() =
    BetsGroupsEntity(
        groupId = groupId,
        description = description,
    )

fun AvailableBetsEntity.toDtoWithBetGroup() =
    AvailableBetWithBetGroupDto(
        availableBetId = availableBetId!!,
        ratio = ratio,
        betGroupDto = betsGroupsEntity.toDto(),
        betsClosed = betsClosed,
        matchId = matchId,
    )

fun FullAvailableBetWithBetGroupDto.toResponse() =
    FullTypeOfBetMatchResponse(
        id = availableBetId,
        match = match.toResponse(),
        group = betGroupDto.toResponse(),
        ratio = ratio,
    )


fun MatchDto.toResponse() = MatchResponse(
    id = matchId,
    name = name,
    sport = sport.toResponse(),
    ended = ended,
)

fun SportDto.toResponse(): SportResponse =
    SportResponse(
        id = sportId,
        name = name,
    )

fun BetDto.toResponse() =
    BetResponse(
        id = betId,
        ratio = ratio,
        amount = amount,
        userId = userId,
        availableBetId = availableBetId,
    )

fun BetsGroupsEntity.toDto() =
    BetGroupDto(
        groupId = groupId!!,
        description = description,
    )

fun BetGroupDto.toResponse() =
    BetGroupResponse(
        id = groupId,
        description = description,
    )

fun AvailableBetWithBetGroupDto.toAvailableBetDto() =
    AvailableBetDto(
        availableBetId = availableBetId,
        ratio = ratio,
        groupId = betGroupDto.groupId,
        betsClosed = betsClosed,
        matchId = matchId
    )
