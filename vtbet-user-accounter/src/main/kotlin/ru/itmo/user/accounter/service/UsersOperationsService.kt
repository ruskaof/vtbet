package ru.itmo.user.accounter.service

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.itmo.common.dto.UserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.user.accounter.repository.UsersRepository

@Service
class UsersOperationsService(
    private val usersRepository: UsersRepository,
    private val databaseClient: DatabaseClient,
) {
    fun getUser(userId: Long): Mono<UserDto> =
        usersRepository.findByUserId(userId).map { it.toDto() }

    fun getUserWithPassword(userId: Long): Mono<UserWithPasswordDto> =
        usersRepository.findByUserId(userId).map { it.toWithPasswordDto() }

    fun getByUserName(username: String): Mono<UserDto> =
        usersRepository.findByUsername(username).map { it.toDto() }

    fun save(userDto: UserWithPasswordDto): Mono<UserDto> =
        usersRepository.getNextUserId()
            .flatMap {
                usersRepository.save(
                    userDto
                        .copy(userId = it!!)
                        .toEntity()
                ).map { user -> user.toDto() }
            }

    fun saveWithId(userDto: UserWithPasswordDto): Mono<UserDto> =
            usersRepository.save(
                userDto.toEntity()
            ).map { user -> user.toDto() }

    fun update(userDto: UserDto): Mono<UserDto> =
        usersRepository.save(
            userDto.toEntity()
        ).map { it.toDto() }

    fun deleteByUserId(userId: Long) =
        usersRepository.deleteByUserId(userId)
}
