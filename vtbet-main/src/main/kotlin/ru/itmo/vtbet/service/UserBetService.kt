package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.IllegalBetException
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.request.MakeBetRequest
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.repository.UsersRepository

/**
 * Тут логика работы со ставками пользователей
 * То есть приходит пользователь, выбирает матч и по конкретному match_id
 * и bet делает свою ставку (в базу type_of_bet_match)
 */
@Service
class UserBetService(
    private val usersRepository: UsersRepository,
    private val userAccountRepository: UserAccountRepository,
    private val betRepository: BetsRepository,
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository,
) {
    @Transactional
    fun makeBet(id: Long, makeBetRequest: MakeBetRequest): BetDto {
        val userAccount = userAccountRepository.findById(makeBetRequest.userId)
            .orElseThrow { ResourceNotFoundException("No user found with ID: ${makeBetRequest.userId}") }

        val user = usersRepository.findById(makeBetRequest.userId)
            .orElseThrow { ResourceNotFoundException("No user found with ID: ${makeBetRequest.userId}") }

        val typeOfBetMatch = typeOfBetMatchRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No bet found with ID: $id") }

        if (typeOfBetMatch.ratioNow.stripTrailingZeros() != makeBetRequest.ratio.stripTrailingZeros()) {
            throw IllegalBetException("Wrong ratio: ratio now is ${typeOfBetMatch.ratioNow}")
        }

        // Проверка, что сумма ставки не превышает баланс пользователя
        if (userAccount.balanceAmount < makeBetRequest.amount) {
            throw IllegalBetException("Not enough funds to make bet")
        }

        // Произведение ставки
        val accountToSave = userAccount.copy( balanceAmount = userAccount.balanceAmount - makeBetRequest.amount)
        userAccountRepository.save(accountToSave)

        // Запись о ставке в историю ставок
        val bet = BetsEntity(
            id = id,
            amount = makeBetRequest.amount,
            typeOfBetMatch = typeOfBetMatch,
            ratio = makeBetRequest.ratio,
            usersEntity = user,
        )
        betRepository.save(bet)
        return bet.toDto()
    }
}
