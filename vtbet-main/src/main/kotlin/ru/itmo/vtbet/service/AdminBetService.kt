package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.UpdateAvailableBetRequestDto
import java.math.RoundingMode

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
@Service
class AdminBetService(
    private val matchesService: MatchesService,
    private val betsService: BetsService,
    private val availableBetsService: AvailableBetsService,
    private val usersAccountsService: UsersAccountsService,
) {
    @Transactional
    fun updateAvailableBet(betId: Long, updateAvailableBetRequestDto: UpdateAvailableBetRequestDto): AvailableBetDto {
        val bet = availableBetsService.getAvailableBetWithGroup(betId)
            ?: throw ResourceNotFoundException("Bet not found")
        return availableBetsService.update(
            bet.copy(ratio = updateAvailableBetRequestDto.ratio.toBigDecimal())
        )
    }

    @Transactional
    fun createAvailableBet(matchId: Long, request: CreateAvailableBetRequestDto): FullAvailableBetWithBetGroupDto {
        val group = betsService.getBetGroup(request.groupId)
            ?: throw ResourceNotFoundException("Bet group not found")
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match not found")

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
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")

        val allAvailableBetsForMatch = availableBetsService.getAllByMatchId(match.matchId)
        val allAvailableBetIds = allAvailableBetsForMatch.map { it.availableBetId }
        val allBetsForMatch = betsService.getBetsByAvailableBetIds(allAvailableBetIds)

        allBetsForMatch.asSequence()
            .filter { it.availableBetId in successfulBets }
            .forEach {
                val userAccount = usersAccountsService.getComplexUserAccount(it.userId) ?: return@forEach
                usersAccountsService.save(
                    userAccount.copy(
                        balanceAmount = (userAccount.balanceAmount + (it.amount * it.ratio)).scaled()
                    )
                )
            }
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
