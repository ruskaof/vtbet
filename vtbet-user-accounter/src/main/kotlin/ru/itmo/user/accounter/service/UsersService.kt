package ru.itmo.user.accounter.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.common.exception.DuplicateException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.utils.scaled
import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import java.math.BigDecimal

import java.time.Instant
import java.time.ZoneOffset

@Service
class ComplexUsersService(
    private val usersOperationsService: UsersOperationsService,
    private val usersAccountsService: UsersAccountsService,
    @Value("\${vtbet.ratio-decrease-value}")
    private val ratioDecreaseValue: BigDecimal,
) {
    @Transactional
    fun getUser(userId: Long): ComplexUserDto? {
        val user = usersOperationsService.getUser(userId) ?: return null
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
        if (usersOperationsService.getByUserName(request.username) != null) {
            throw DuplicateException("Username already exists")
        }
        val user = usersOperationsService.save(
            UserDto(
                userId = 0,
                username = request.username,
                email = request.email,
                phoneNumber = request.phoneNumber,
                accountVerified = true,
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
            accountVerified = true,
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
                accountVerified = true,
                registrationDate = user.registrationDate,
            )
        )

        return complexUser.copy(accountId = userAccount.accountId)
    }

    @Transactional
    fun deleteUser(userId: Long) {
        usersOperationsService.getUser(userId) ?: throw ResourceNotFoundException("User with userId $userId not found")
        usersOperationsService.deleteById(userId)
    }

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequestDto): ComplexUserDto {
        var userToUpdate = usersOperationsService.getUser(userId)
            ?: throw ResourceNotFoundException("User with userId $userId not found")
        val userAccount = usersAccountsService.getUserAccount(userId)
            ?: throw ResourceNotFoundException("User account with userId $userId not found")

        request.username?.let {
            if (usersOperationsService.getByUserName(it) != null && userToUpdate.username != it) {
                throw DuplicateException("Username already exists")
            }
            userToUpdate = userToUpdate.copy(username = it)
        }
        request.email?.let { userToUpdate = userToUpdate.copy(email = it) }
        request.phoneNumber?.let { userToUpdate = userToUpdate.copy(phoneNumber = it) }
        request.isVerified?.let { userToUpdate = userToUpdate.copy(accountVerified = it) }

        usersOperationsService.update(userToUpdate)

        return ComplexUserDto(userToUpdate, userAccount)
    }

    @Transactional
    fun handleBalanceAction(userId: Long, amount: BigDecimal, action: BalanceActionType): ComplexUserDto {
        val userAccount = getUser(userId)
            ?: throw ResourceNotFoundException("User account with userId $userId not found")

        val updatedAccount = when (action) {
            BalanceActionType.DEPOSIT -> userAccount.copy(
                balanceAmount = userAccount.balanceAmount.add(amount).scaled(),
            )
            BalanceActionType.WITHDRAW -> userAccount.copy(
                balanceAmount = userAccount.balanceAmount.subtract(amount).scaled(),
            )
        }

        usersAccountsService.update(updatedAccount)

        return updatedAccount
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
}