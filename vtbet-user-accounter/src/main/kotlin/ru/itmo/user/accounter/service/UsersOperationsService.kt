package ru.itmo.user.accounter.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.itmo.common.dto.UserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.user.accounter.repository.UsersRepository

@Service
class UsersOperationsService(
    private val usersRepository: UsersRepository,
) {
    fun getUser(userId: Long) =
        usersRepository.findById(userId).map { it.toDto() }

    fun getByUserName(username: String): Mono<UserDto> =
        usersRepository.findByUsername(username).map { it.toDto() }

    fun save(userDto: UserWithPasswordDto) =
        usersRepository.save(
            userDto
                .toEntity()
                .copy(userId = null)
        ).map { it.toDto() }

    fun update(userDto: UserDto) =
        usersRepository.save(
            userDto.toEntity()
        ).map { it.toDto() }

    fun deleteById(userId: Long) = usersRepository.deleteById(userId)
}
