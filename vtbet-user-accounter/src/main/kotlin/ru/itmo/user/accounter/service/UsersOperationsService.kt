package ru.itmo.user.accounter.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.user.accounter.repository.UsersRepository
import kotlin.jvm.optionals.getOrNull

@Service
class UsersOperationsService(
    private val usersRepository: UsersRepository,
) {
    fun getUser(userId: Long) =
        usersRepository.findById(userId).map { it.toDto() }

    fun getByUserName(username: String) =
        usersRepository.findByUsername(username).map { it.toDto() }

    fun save(userDto: UserDto) =
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
