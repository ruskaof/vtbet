package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.IllegalBetActionException
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.request.UpdateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.response.SimpleTypeOfBetMatchResponse
import kotlin.jvm.optionals.getOrElse

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
@Service
class AdminBetService(
    private val betService: BetService,
    private val matchService: MatchService,
    private val userService: UserService,
) {

    @Transactional
    fun closeMatchBets(matchId: Long) {
        val match = matchService.getMatch(matchId)
            ?: throw ResourceNotFoundException("No Match found with ID: $matchId")

        betService.getMatchAvailableBets(matchId).forEach {
            betService.closeAvailableBet(it.id)
        }
    }

    @Transactional
    fun submitMatchResult(matchId: Long, successfulBets: Set<Long>) {
        val match = matchService.getMatch(matchId)
            ?: throw ResourceNotFoundException("No Match found with ID: $matchId")

        if (match.ended) {
            throw IllegalBetActionException("Match with ID: $matchId has already ended")
        }

        val allBetsForMatch = betService.getMatchBets(matchId)

        allBetsForMatch.filter { it.id in successfulBets }
            .forEach {
                userService.addMoneyToUser(it.userId, it.amount * it.ratio)
            }

        matchService.endMatch(matchId)
    }
}
