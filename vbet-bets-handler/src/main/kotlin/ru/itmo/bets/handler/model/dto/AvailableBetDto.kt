package ru.itmo.bets.handler.model.dto

import ru.itmo.common.dto.BetGroupDto
import java.math.BigDecimal

data class AvailableBetDto(
    val availableBetId: Long,
    val ratio: BigDecimal,
    val groupId: Long,
    val betsClosed: Boolean,
    val matchId: Long,
)

data class AvailableBetWithBetGroupDto(
    val availableBetId: Long,
    val ratio: BigDecimal,
    val betGroupDto: BetGroupDto,
    val betsClosed: Boolean,
    val matchId: Long,
)

data class FullAvailableBetWithBetGroupDto(
    val availableBetId: Long,
    val ratio: BigDecimal,
    val betsClosed: Boolean,
    val betGroupDto: BetGroupDto,
    val match: MatchDto,
)
