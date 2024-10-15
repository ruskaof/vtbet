package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.IllegalBetActionException
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.dto.ComplexUserDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.model.request.MakeBetRequestDto
import ru.itmo.vtbet.model.request.UpdateUserRequestDto
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset

@Service
class ComplexUsersService(
    private val usersService: UsersService,
    private val usersAccountsService: UsersAccountsService,
    private val availableBetsService: AvailableBetsService,
    private val matchesService: MatchesService,
    private val betsService: BetsService,
) {
    @Transactional
    fun getUser(userId: Long): ComplexUserDto? {
        val user = usersService.getUser(userId) ?: return null
        val userAccount = usersAccountsService.getUserAccount(userId) ?: return null

        return ComplexUserDto(
            userId = user.userId,
            accountId = userAccount.accountId,
            registrationDate = user.registrationDate.atOffset(ZoneOffset.ofHours(3)).toInstant(),
            balanceAmount = userAccount.balanceAmount,
            username = user.username,
            email = user.email,
            phoneNumber = user.phoneNumber,
            accountVerified = user.accountVerified,
        )
    }

    @Transactional
    fun createUser(
        request: CreateUserRequestDto,
    ): ComplexUserDto {
        if (usersService.getByUserName(request.username) != null) {
            // TODO: throw custom our own exception
            throw IllegalArgumentException("Username already exists")
        }
        val user = usersService.save(
            UserDto(
                userId = 0,
                username = request.username,
                email = request.email,
                phoneNumber = request.phoneNumber,
                // TODO: нужно написать ручку верификации (простое проставление true)
                accountVerified = false,
                registrationDate = Instant.now(),
            )
        )

        val complexUser = ComplexUserDto(
            accountId = 0,
            userId = user.userId,
            balanceAmount = BigDecimal.ZERO,
            username = request.username,
            email = request.email,
            phoneNumber = request.phoneNumber,
            accountVerified = false,
            registrationDate = user.registrationDate,
        )

        val userAccount = usersAccountsService.save(
            ComplexUserDto(
                accountId = 0,
                userId = user.userId,
                balanceAmount = BigDecimal.ZERO,
                username = request.username,
                email = request.email,
                phoneNumber = request.phoneNumber,
                accountVerified = false,
                registrationDate = user.registrationDate,
            )
        )

        return complexUser.copy(accountId = userAccount.accountId)
    }

    @Transactional
    fun deleteUser(userId: Long) {
        usersService.getUser(userId) ?: throw ResourceNotFoundException("User with userId $userId not found")
        usersService.deleteById(userId)
    }

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequestDto): UserDto {
        var userToUpdate = usersService.getUser(userId)
            ?: throw ResourceNotFoundException("User with userId $userId not found")

        request.username?.let {
            if (usersService.getByUserName(it) != null) {
                throw IllegalArgumentException("Username already exists")
            }
            userToUpdate = userToUpdate.copy(username = it)
        }
        request.email?.let { userToUpdate = userToUpdate.copy(email = it) }
        request.phoneNumber?.let { userToUpdate = userToUpdate.copy(phoneNumber = it) }
        request.isVerified?.let { userToUpdate = userToUpdate.copy(accountVerified = it) }

        usersService.update(userToUpdate)
        return userToUpdate
    }

    @Transactional
    fun addMoneyToUser(userId: Long, amount: BigDecimal) {
        val userAccount = getUser(userId)
            ?: throw ResourceNotFoundException("User with userId $userId not found")

        usersAccountsService.update(
            userAccount.copy(
                balanceAmount = userAccount.balanceAmount.add(amount)
            )
        )
    }

    @Transactional
    fun subtractMoneyFromUser(userId: Long, amount: BigDecimal) {
        val userAccount = getUser(userId)
            ?: throw ResourceNotFoundException("User with userId $userId not found")

        usersAccountsService.update(
            userAccount.copy(
                balanceAmount = userAccount.balanceAmount.subtract(amount)
            )
        )
    }

    @Transactional
    fun makeBet(userId: Long, makeBetRequestDto: MakeBetRequestDto): BetDto {
        val user = usersService.getUser(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")
        val userAccount = usersAccountsService.getUserAccount(userId)
            ?: throw ResourceNotFoundException("No user found with ID: $userId")

        if (userAccount.balanceAmount < makeBetRequestDto.amount) {
            throw IllegalBetActionException("User $userId doesn't have enough money to make bet")
        }

        val availableBet = availableBetsService.getAvailableBet(makeBetRequestDto.availableBetId)
            ?: throw ResourceNotFoundException("No available bet found with ID: ${makeBetRequestDto.availableBetId}")
        if (availableBet.betsClosed) {
            throw IllegalBetActionException(
                "Bets on available bet with ID: ${makeBetRequestDto.availableBetId} are closed",
            )
        }

        val match = matchesService.getMatch(availableBet.matchId)
            ?: throw ResourceNotFoundException("No match found with ID: ${availableBet.matchId}")
        if (match.ended) {
            throw IllegalBetActionException("Match has been already finished")
        }

        if (makeBetRequestDto.ratio != availableBet.ratio) {
            throw IllegalBetActionException("Wrong ratio: ratio now is ${availableBet.ratio}")
        }
        val bet = betsService.createBet(user, availableBet, makeBetRequestDto.ratio, makeBetRequestDto.amount)
        subtractMoneyFromUser(userId, makeBetRequestDto.amount)
        return bet
    }
}