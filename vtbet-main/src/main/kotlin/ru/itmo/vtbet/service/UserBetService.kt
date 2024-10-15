package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.IllegalBetActionException
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.request.MakeBetRequestDto

/**
 * Тут логика работы со ставками пользователей
 * То есть приходит пользователь, выбирает матч и по конкретному match_id
 * и bet делает свою ставку (в базу type_of_bet_match)
 */
@Service
class UserBetService(
    private val usersService: UsersService,
    private val betsService: BetsService,
) {
    @Transactional
    fun makeBet(userId: Long, makeBetRequestDto: MakeBetRequestDto): BetDto {
        val user = usersService.getUser(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")

        if (user.balanceAmount < makeBetRequestDto.amount) {
            throw IllegalBetActionException("User $userId doesn't have enough money to make bet")
        }

        val availableBet = betsService.getAvailableBet(makeBetRequestDto.availableBetId)
            ?: throw ResourceNotFoundException("No available bet found with ID: ${makeBetRequestDto.availableBetId}")

        if (availableBet.betsClosed) {
            throw IllegalBetActionException(
                "Bets on available bet with ID: ${makeBetRequestDto.availableBetId} are closed",
            )
        }

        if (makeBetRequestDto.ratio != availableBet.ratio) {
            throw IllegalBetActionException("Wrong ratio: ratio now is ${availableBet.ratio}")
        }

        val bet = betsService.createBet(user, availableBet, makeBetRequestDto.ratio, makeBetRequestDto.amount)

        usersService.subtractMoneyFromUser(user.id, makeBetRequestDto.amount)

        return bet
    }
}
