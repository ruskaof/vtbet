package ru.itmo.bets.handler.service

import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccountClient
import ru.itmo.bets.handler.request.MakeBetRequestDto
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.utils.scaled
import java.math.BigDecimal

@Service
class ComplexUsersService(
    private val sportsClient: SportsClient,
    private val userAccountClient: UserAccountClient,
    private val availableBetsService: AvailableBetsService,
    private val betsService: BetsService,
    @Value("\${vtbet.ratio-decrease-value}")
    private val ratioDecreaseValue: BigDecimal,
) {
    @Transactional
    fun makeBet(userId: Long, makeBetRequestDto: MakeBetRequestDto): BetDto {

        val user = try {
            userAccountClient.getUser(userId).toDto()
        } catch (e: FeignException) {
            if (e.status() == 404) {
                throw ResourceNotFoundException("User account not found")
            } else {
                throw e
            }
        }

        val availableBet = availableBetsService.getAvailableBetWithGroup(makeBetRequestDto.availableBetId)
            ?: throw ResourceNotFoundException("No available bet found with ID: ${makeBetRequestDto.availableBetId}")
        if (availableBet.betsClosed) {
            throw IllegalBetActionException(
                "Bets on available bet with ID: ${makeBetRequestDto.availableBetId} are closed",
            )
        }

        val match = try { sportsClient.getMatch(availableBet.matchId) } catch (e: FeignException) {
            if (e.status() == 404) {
                throw ResourceNotFoundException("Match not found")
            } else {
                throw e
            }
        }

        if (match.ended) {
            throw IllegalBetActionException("Match has been already finished")
        }

        if (makeBetRequestDto.ratio.scaled() != availableBet.ratio.scaled()) {
            throw IllegalBetActionException("Wrong ratio: ratio now is ${availableBet.ratio}")
        }
        val bet = betsService.createBet(
            user.userId,
            availableBet.toAvailableBetDto(),
            makeBetRequestDto.ratio.scaled(),
            makeBetRequestDto.amount
        )

        userAccountClient.updateBalance(
            userId,
            BalanceActionRequestDto(
                BalanceActionType.WITHDRAW,
                makeBetRequestDto.amount
            )
        )

        availableBetsService.update(availableBet.copy(ratio = updateRatio(availableBet.ratio)))

        return bet
    }

    fun getUserBets(userId: Long, pageNumber: Int, pageSize: Int): PagingDto<BetDto> {
        return betsService.getUserBets(userId, pageNumber, pageSize)
    }

    private fun updateRatio(oldRatio: BigDecimal) =
        maxOf(BigDecimal.ONE, oldRatio - ratioDecreaseValue).scaled()
}