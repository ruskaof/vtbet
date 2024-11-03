package ru.itmo.bets.handler.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UsersClient
import ru.itmo.bets.handler.exception.IllegalBetActionException
import ru.itmo.bets.handler.exception.ResourceNotFoundException
import ru.itmo.bets.handler.request.MakeBetRequestDto
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneOffset

@Service
class ComplexUsersService(
    private val sportsClient: SportsClient,
    private val usersClient: UsersClient,
    private val availableBetsService: AvailableBetsService,
    private val betsService: BetsService,
    @Value("\${vtbet.ratio-decrease-value}")
    private val ratioDecreaseValue: BigDecimal,
) {
    @Transactional
    fun makeBet(userId: Long, makeBetRequestDto: MakeBetRequestDto): BetDto {
        val user = usersClient.getUser(userId).toDto()

        val availableBet = availableBetsService.getAvailableBetWithGroup(makeBetRequestDto.availableBetId)
            ?: throw ResourceNotFoundException("No available bet found with ID: ${makeBetRequestDto.availableBetId}")
        if (availableBet.betsClosed) {
            throw IllegalBetActionException(
                "Bets on available bet with ID: ${makeBetRequestDto.availableBetId} are closed",
            )
        }

        //FIXME: sasaovch handle not found
        val match = sportsClient.getMatch(availableBet.matchId)
//            ?: throw ResourceNotFoundException("No match found with ID: ${availableBet.matchId}")
        if (match.ended) {
            throw IllegalBetActionException("Match has been already finished")
        }

        if (makeBetRequestDto.ratio.scaled() != availableBet.ratio.scaled()) {
            throw IllegalBetActionException("Wrong ratio: ratio now is ${availableBet.ratio}")
        }
        val bet = betsService.createBet(
            user,
            availableBet.toAvailableBetDto(),
            makeBetRequestDto.ratio.scaled(),
            makeBetRequestDto.amount
        )

        usersClient.updateBalance(
            userId,
            BalanceActionRequestDto(
                BalanceActionType.WITHDRAW,
                makeBetRequestDto.amount
            )
        )

        availableBetsService.update(availableBet.copy(ratio = updateRatio(availableBet.ratio)))

        return bet
    }

    val MAX_PAGE_SIZE = 50L
    val DECIMAL_SCALE = 2

    fun BigDecimal.scaled() = setScale(DECIMAL_SCALE, RoundingMode.FLOOR)


    fun getUserBets(userId: Long, pageNumber: Int, pageSize: Int): PagingDto<BetDto> {
        return betsService.getUserBets(userId, pageNumber, pageSize)
    }

    private fun updateRatio(oldRatio: BigDecimal) =
        maxOf(BigDecimal.ONE, oldRatio - ratioDecreaseValue).scaled()
}