package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.ComplexUserDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset

@Service
class ComplexUsersService(
    private val usersService: UsersService,
    private val usersAccountsService: UsersAccountsService,
) {
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
    fun updateUser(userId: Long, request: CreateUserRequestDto): UserDto {
        // TODO: сделать проверку: нельзя менять username (нужна доп валидация, что не занят)
        var userToUpdate = usersService.getUser(userId)
            ?: throw ResourceNotFoundException("User with userId $userId not found")

        request.email?.let { userToUpdate = userToUpdate.copy(email = it) }

        request.phoneNumber?.let { userToUpdate = userToUpdate.copy(phoneNumber = it) }

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
}