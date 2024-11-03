package ru.itmo.bets.handler.service

import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.response.*

fun BetsEntity.toDto() =
    BetDto(
        betId = this.betId!!,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.usersEntity.userId!!,
        availableBetId = this.availableBetId,
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
