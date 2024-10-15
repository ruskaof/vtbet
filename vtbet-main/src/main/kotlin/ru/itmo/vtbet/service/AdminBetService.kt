package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.IllegalBetActionException
import ru.itmo.vtbet.exception.ResourceNotFoundException

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
@Service
class AdminBetService(
    private val betsService: BetsService,
    private val matchesService: MatchesService,
    private val usersService: UsersService,
) {

    @Transactional
    fun closeMatchBets(matchId: Long) {
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("No Match found with ID: $matchId")

        betsService.getMatchAvailableBets(matchId).forEach {
            betsService.closeAvailableBet(it.availableBetId)
        }
    }

    @Transactional
    fun submitMatchResult(matchId: Long, successfulBets: Set<Long>) {
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("No Match found with ID: $matchId")

        if (match.ended) {
            throw IllegalBetActionException("Match with ID: $matchId has already ended")
        }

        val allBetsForMatch = betsService.getMatchBets(matchId)

        allBetsForMatch.filter { it.betId in successfulBets }
            .forEach {
                usersService.addMoneyToUser(it.userId, it.amount * it.ratio)
            }

        matchesService.endMatch(matchId)
    }
}
