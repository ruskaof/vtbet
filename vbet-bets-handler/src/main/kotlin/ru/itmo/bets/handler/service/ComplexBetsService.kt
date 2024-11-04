package ru.itmo.bets.handler.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccountClient
import ru.itmo.bets.handler.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.request.UpdateAvailableBetRequestDto
import ru.itmo.common.dto.*
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.response.MatchResponse
import ru.itmo.common.response.SportResponse
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

//    fun getAvailableBets(pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
//        val result = availableBetsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))
//        return PagingDto(
//            items = result.content.map { it.toDto() },
//            total = result.totalElements,
//            pageSize = pageSize,
//            page = pageNumber,
//        )
//    }

    fun SportResponse.toDto(): SportDto {
        return SportDto(
            sportId = this.id,
            name = this.name,
        )
    }

    fun MatchResponse.toDto(): MatchDto {
        return MatchDto(
            matchId = this.id,
            ended = this.ended,
            name = this.name,
            sport = this.sport.toDto(),
        )
    }

    @Transactional
    fun createAvailableBet(matchId: Long, request: CreateAvailableBetRequestDto): FullAvailableBetWithBetGroupDto {
        val group = betsService.getBetGroup(request.groupId)
            ?: throw ResourceNotFoundException("Bet group not found")
        //FIXME: sasaovch what if null or not found or exception
        val match = sportsClient.getMatch(matchId).toDto()

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

        sportsClient.endMatch(matchId, true)
    }
}
