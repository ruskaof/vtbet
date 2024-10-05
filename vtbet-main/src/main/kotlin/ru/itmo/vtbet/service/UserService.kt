package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.repository.UsersRepository
import java.math.BigDecimal
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val usersRepository: UsersRepository,
    private val userAccountRepository: UserAccountRepository,
) {

    fun getUser(id: Long): UserDto {
        val usersEntity = usersRepository.findById(id).getOrNull()
        val userAccountEntity = userAccountRepository.findById(id).getOrNull()

        if (usersEntity == null || userAccountEntity == null) {
            throw ResourceNotFoundException("User with id $id not found")
        }

        return UserDto(
            id = usersEntity.id!!,
            registrationDate = usersEntity.registrationDate,
            balanceAmount = userAccountEntity.balanceAmount,
            username = userAccountEntity.username,
            email = userAccountEntity.email,
            phoneNumber = userAccountEntity.phoneNumber,
        )
    }

    @Transactional
    fun createUser(
        request: CreateUserRequest,
    ): UserDto {
        val usersEntity = usersRepository.save(UsersEntity(registrationDate = Instant.now()))
        val userAccountEntity = UserAccountEntity(
            userId = usersEntity.id!!,
            balanceAmount = BigDecimal.ZERO,
            username = request.username,
            email = request.email,
            phoneNumber = request.phoneNumber,
            accountVerified = true,
        )
        userAccountRepository.save(userAccountEntity)

        return UserDto(
            id = usersEntity.id,
            registrationDate = usersEntity.registrationDate,
            balanceAmount = userAccountEntity.balanceAmount,
            username = userAccountEntity.username,
            email = userAccountEntity.email,
            phoneNumber = userAccountEntity.phoneNumber,
        )
    }

    @Transactional
    fun addMoneyToUser(userId: Long, amount: BigDecimal) {
        val userAccountEntity = userAccountRepository.findById(userId).getOrNull()
            ?: throw ResourceNotFoundException("User with id $userId not found")

        userAccountEntity.balanceAmount = userAccountEntity.balanceAmount.add(amount)
        userAccountRepository.save(userAccountEntity)
    }

    @Transactional
    fun subtractMoneyFromUser(userId: Long, amount: BigDecimal) {
        val userAccountEntity = userAccountRepository.findById(userId).getOrNull()
            ?: throw ResourceNotFoundException("User with id $userId not found")

        userAccountEntity.balanceAmount = userAccountEntity.balanceAmount.subtract(amount)
        userAccountRepository.save(userAccountEntity)
    }
}