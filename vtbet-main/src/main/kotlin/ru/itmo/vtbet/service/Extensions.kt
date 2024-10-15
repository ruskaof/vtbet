package ru.itmo.vtbet.service

import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.response.*

//FIXME: нужно бы разнести эти расширения по классам
fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        accountId = accountId!!,
        userId = usersEntity.userId!!,
        balanceAmount = balanceAmount,
    )

fun UsersAccountsEntity.toComplexDto() =
    ComplexUserDto(
        userId = usersEntity.userId!!,
        accountId = accountId!!,
        registrationDate = usersEntity.registrationDate,
        balanceAmount = balanceAmount,
        username = usersEntity.username,
        email = usersEntity.email,
        phoneNumber = usersEntity.phoneNumber,
        accountVerified = usersEntity.accountVerified,
    )

fun UsersEntity.toDto() =
    UserDto(
        userId = userId!!,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

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
        //FIXME
        ratio = ratio.toPlainString(),
        betsClosed = betsClosed,
    )

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

fun ComplexUserDto.toResponse() =
    UserResponse(
        id = userId,
        registrationDate = registrationDate,
        // FIXME: проверить конвертацию
        balanceAmount = balanceAmount.toPlainString(),
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
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

fun UserDto.toEntity() =
    UsersEntity(
        userId = userId,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
    )

fun UserDto.toResponse() =
    UserResponse(
        id = userId,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        accountVerified = accountVerified,
        registrationDate = registrationDate,
        balanceAmount = null,
    )

fun ComplexUserDto.toEntity() =
    UsersAccountsEntity(
        accountId = accountId,
        usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        ),
        balanceAmount = balanceAmount,
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
        //FIXME: проверить
        ratio = ratio.toPlainString(),
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
