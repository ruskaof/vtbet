package ru.itmo.bets.handler.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccounterClient
import ru.itmo.bets.handler.exception.IllegalBetActionException
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.model.request.UpdateAvailableBetRequestDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.utils.scaled

@Service
class AdminBetService(
    private val sportsClient: SportsClient,
    private val userAccounterClient: UserAccounterClient,
    private val betsService: BetsService,
    private val availableBetsService: AvailableBetsService,
) {
    @Transactional
    fun updateAvailableBet(betId: Long, updateAvailableBetRequestDto: UpdateAvailableBetRequestDto): AvailableBetDto {
        val bet = availableBetsService.getAvailableBetWithGroup(betId)
            ?: throw ResourceNotFoundException("Bet not found")
        return availableBetsService.update(
            bet.copy(ratio = updateAvailableBetRequestDto.ratio.scaled())
        )
    }

    @Transactional
    fun createAvailableBet(matchId: Long, request: CreateAvailableBetRequestDto): FullAvailableBetWithBetGroupDto {
        val group = betsService.getBetGroup(request.groupId)
            ?: throw ResourceNotFoundException("Bet group not found")
        val match = sportsClient.getMatch(matchId)

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
                userAccounterClient.updateBalance(it.userId, BalanceActionRequestDto(
                    BalanceActionType.DEPOSIT,
                    it.amount * it.ratio
                ))
            }

        sportsClient.endMatch(matchId)
    }

    @Transactional
    fun closeBetsForMatch(availableBetId: Long) {
        val bet = availableBetsService.getAvailableBetWithGroup(availableBetId)
            ?: throw ResourceNotFoundException("Available  with id $availableBetId not found")
        availableBetsService.update(bet.copy(betsClosed = true))
    }

    fun getBetGroup(betGroupId: Long): BetGroupDto {
        return betsService.getBetGroup(betGroupId)
            ?: throw ResourceNotFoundException("Bet group with id $betGroupId not found")
    }

    fun getAvailableBet(availableBetId: Long): AvailableBetDto {
        return availableBetsService.getAvailableBet(availableBetId)
            ?: throw ResourceNotFoundException("Available bet with id $availableBetId not found")
    }

    fun getAvailableBets(pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        return availableBetsService.getAvailableBets(pageNumber, pageSize)
    }

    fun getBetGroups(pageNumber: Int, pageSize: Int): PagingDto<BetGroupDto> {
        return betsService.getBetGroups(pageNumber, pageSize)
    }

    @Transactional
    fun deleteAvailableBet(availableBetId: Long) {
        availableBetsService.getAvailableBet(availableBetId)
            ?: throw ResourceNotFoundException("Available bet with id $availableBetId not found")
        availableBetsService.delete(availableBetId)
    }

    @Transactional
    fun deleteBetGroup(betGroupId: Long) {
        betsService.getBetGroup(betGroupId)
            ?: throw ResourceNotFoundException("Bet group with id $betGroupId not found")
        betsService.delete(betGroupId)
    }
}
