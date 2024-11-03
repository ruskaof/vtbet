package ru.itmo.user.accounter.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.user.accounter.model.dto.ComplexUserDto
import ru.itmo.vtbet.repository.UsersAccountsRepository

@Service
class UsersAccountsService(
    private val usersAccountsRepository: UsersAccountsRepository,
) {
    fun getUserAccount(userId: Long) =
        usersAccountsRepository.findByIdOrNull(userId)?.toDto()

    fun getComplexUserAccount(userId: Long) =
        usersAccountsRepository.findByIdOrNull(userId)?.toComplexDto()

    fun save(userAccountDto: ComplexUserDto) =
        usersAccountsRepository.saveAndFlush(
            userAccountDto
                .toEntity()
                .copy(accountId = null)
        ).toDto()

    fun update(userAccountDto: ComplexUserDto) =
        usersAccountsRepository.saveAndFlush(
            userAccountDto.toEntity()
        ).toDto()
}