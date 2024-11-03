package ru.itmo.bets.handler.service

import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.request.MakeBetRequestDto
import java.math.BigDecimal

class UserBetsService(

) {
    @Transactional
    fun makeBet(userId: Long, makeBetRequestDto: MakeBetRequestDto): BetDto {
        val user = usersOperationsService.getUser(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")
        val userAccount = usersAccountsService.getUserAccount(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")

        if (userAccount.balanceAmount < makeBetRequestDto.amount) {
            throw IllegalBetActionException("User $userId doesn't have enough money to make bet")
        }

        val availableBet = availableBetsService.getAvailableBetWithGroup(makeBetRequestDto.availableBetId)
            ?: throw ResourceNotFoundException("No available bet found with ID: ${makeBetRequestDto.availableBetId}")
        if (availableBet.betsClosed) {
            throw IllegalBetActionException(
                "Bets on available bet with ID: ${makeBetRequestDto.availableBetId} are closed",
            )
        }

        val match = matchesOperationsService.getMatch(availableBet.matchId)
            ?: throw ResourceNotFoundException("No match found with ID: ${availableBet.matchId}")
        if (match.ended) {
            throw IllegalBetActionException("Match has been already finished")
        }

        if (makeBetRequestDto.ratio.scaled() != availableBet.ratio.scaled()) {
            throw IllegalBetActionException("Wrong ratio: ratio now is ${availableBet.ratio}")
        }
        val bet = betsService.createBet(user, availableBet.toAvailableBetDto(), makeBetRequestDto.ratio.scaled(), makeBetRequestDto.amount)
        subtractMoneyFromUser(userId, makeBetRequestDto.amount)

        availableBetsService.update(availableBet.copy(ratio = updateRatio(availableBet.ratio)))

        return bet
    }

    private fun updateRatio(oldRatio: BigDecimal) =
        maxOf(BigDecimal.ONE, oldRatio - ratioDecreaseValue).scaled()

    fun getUserBets(userId: Long, pageNumber: Int, pageSize: Int): PagingDto<BetDto> {
        usersOperationsService.getUser(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")
        return betsService.getUserBets(userId, pageNumber, pageSize)
    }
}