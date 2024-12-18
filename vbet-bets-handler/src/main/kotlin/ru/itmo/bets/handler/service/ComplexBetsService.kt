package ru.itmo.bets.handler.service

import feign.FeignException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccountClient
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.bets.handler.model.dto.FullAvailableBetWithBetGroupDto
import ru.itmo.bets.handler.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.request.UpdateAvailableBetRequestDto
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.utils.scaled

@Service
class ComplexBetsService(
    private val sportsClient: SportsClient,
    private val userAccountClient: UserAccountClient,
    private val betsService: BetsService,
    private val availableBetsService: AvailableBetsService,
) {
    fun getAvailableBets(pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        return availableBetsService.getAvailableBets(pageNumber, pageSize)
    }

    fun getAvailableBet(availableBetId: Long): AvailableBetDto {
        return availableBetsService.getAvailableBet(availableBetId)
            ?: throw ResourceNotFoundException("Available bet with id $availableBetId not found")
    }

    @Transactional
    fun updateAvailableBet(betId: Long, updateAvailableBetRequestDto: UpdateAvailableBetRequestDto): AvailableBetDto {
        val bet = availableBetsService.getAvailableBetWithGroup(betId)
            ?: throw ResourceNotFoundException("Bet not found")
        return availableBetsService.update(
            bet.copy(ratio = updateAvailableBetRequestDto.ratio.scaled())
        )
    }

    @Transactional
    fun deleteAvailableBet(availableBetId: Long) {
        availableBetsService.getAvailableBet(availableBetId)
            ?: throw ResourceNotFoundException("Available bet with id $availableBetId not found")
        availableBetsService.delete(availableBetId)
    }

    @Transactional
    fun closeBetsForMatch(availableBetId: Long) {
        val bet = availableBetsService.getAvailableBetWithGroup(availableBetId)
            ?: throw ResourceNotFoundException("Available  with id $availableBetId not found")
        availableBetsService.update(bet.copy(betsClosed = true))
    }


    @Transactional
    fun createAvailableBet(matchId: Long, request: CreateAvailableBetRequestDto): FullAvailableBetWithBetGroupDto {
        val group = betsService.getBetGroup(request.groupId)
            ?: throw ResourceNotFoundException("Bet group not found")
        val match = try {
            sportsClient.getMatch(matchId).toDto()
        } catch (e: FeignException) {
            if (e.status() == 404) {
                throw ResourceNotFoundException("Match not found")
            } else {
                throw e
            }
        }

        val scaledRatio = request.ratio.scaled()

        val bet = availableBetsService.save(
            AvailableBetWithBetGroupDto(
                availableBetId = 0,
                ratio = scaledRatio,
                betsClosed = false,
                matchId = matchId,
                betGroupDto = BetGroupDto(
                    groupId = group.groupId,
                    description = group.description,
                ),
            )
        )
        return FullAvailableBetWithBetGroupDto(
            availableBetId = bet.availableBetId,
            ratio = scaledRatio,
            betsClosed = bet.betsClosed,
            betGroupDto = bet.betGroupDto,
            match = match,
        )
    }

    @Transactional
    fun countResultsForMatch(matchId: Long, successfulBets: Set<Long>) {
        val match = sportsClient.getMatch(matchId)

        if (match.ended) {
            throw IllegalBetActionException("Match with id $matchId is already ended")
        }

        val allAvailableBetsForMatch = availableBetsService.getAllByMatchId(match.id)
        val allAvailableBetIds = allAvailableBetsForMatch.map { it.availableBetId }
        val allBetsForMatch = betsService.getBetsByAvailableBetIds(allAvailableBetIds)

        allBetsForMatch.asSequence()
            .filter { it.availableBetId in successfulBets }
            .forEach {
                userAccountClient.updateBalance(
                    it.userId, BalanceActionRequestDto(
                        BalanceActionType.DEPOSIT,
                        it.amount * it.ratio
                    )
                )
            }

        sportsClient.endMatch(matchId)
    }
}
