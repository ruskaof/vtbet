package ru.itmo.user.accounter.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.itmo.common.dto.ComplexUserDto
import ru.itmo.common.dto.UserAccountDto
import ru.itmo.user.accounter.repository.UsersAccountsRepository

@Service
class UsersAccountsService(
    private val usersAccountsRepository: UsersAccountsRepository,
) {
    fun getUserAccount(userId: Long): Mono<UserAccountDto> =
        usersAccountsRepository.findById(userId).map { it.toDto() }

    fun save(userAccountDto: ComplexUserDto) =
        usersAccountsRepository.save(
            userAccountDto
                .toEntity()
                .copy(userId = null)
        ).map { it.toDto() }

    fun update(userAccountDto: ComplexUserDto) =
        usersAccountsRepository.save(
            userAccountDto.toEntity()
        ).map { it.toDto() }
}