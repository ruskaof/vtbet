package ru.itmo.vtbet.service

import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.response.BetGroupResponse
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SimpleAvailableBetsResponse
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.model.response.UserResponse

fun UsersAccountsEntity.toDto() =
    UserAccountDto(
        accountId = accountId!!,
        userId = usersEntity.userId!!,
        balanceAmount = balanceAmount,
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
        betId = this.id!!,
        ratio = this.ratio,
        amount = this.amount,
        userId = this.usersEntity.id!!,
        availableBetId = this.availableBetId,
    )

//fun BetGroup.toResponse(): TypeOfBetResponse = TypeOfBetResponse(
//    id = id,
//    description = description
//)

fun BetGroupDto.toResponse(): BetGroupResponse = BetGroupResponse(
    id = groupId,
    typeOfBets = typeOfBets.map(BetGroup::toResponse)
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

fun SimpleAvailableBetsDto.toResponse() =
    SimpleAvailableBetsResponse(
        id = this.availableBetId,
        ratio = this.ratio.toPlainString(),
        matchId = this.matchId,
        groupId = this.groupId,
        betsClosed = this.betsClosed,
    )

//fun UserDto.toResponse() = UserResponse(
//    id = id,
//    registrationDate = registrationDate,
//    balanceAmount = balanceAmount,
//    username = username,
//    email = email,
//    phoneNumber = phoneNumber,
//)

fun ComplexUserDto.toResponse() =
    UserResponse(
        id = userId,
        registrationDate = registrationDate,
        balanceAmount = balanceAmount,
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
    AvailableBetDto(id!!, ratioNow, typeOfBetId, betsClosed, matchId)

fun AvailableBetsEntity.toSimpleDto() =
    SimpleAvailableBetsDto(
        availableBetId = availableBetId!!,
        ratio = ratio,
        betsClosed = betsClosed,
        matchId = matchId,
        groupId = betsGroupsEntity.groupId!!,
    )