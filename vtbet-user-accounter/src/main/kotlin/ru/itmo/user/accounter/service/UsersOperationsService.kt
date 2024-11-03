package ru.itmo.user.accounter.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.repository.UsersRepository
import kotlin.jvm.optionals.getOrNull

@Service
class UsersOperationsService(
    private val usersRepository: UsersRepository,
) {
    fun getUser(userId: Long) =
        usersRepository.findByIdOrNull(userId)?.toDto()

    fun getByUserName(username: String) =
        usersRepository.findByUsername(username).getOrNull()?.toDto()

    fun save(userDto: UserDto) =
        usersRepository.saveAndFlush(
            userDto
                .toEntity()
                .copy(userId = null)
        ).toDto()

    fun update(userDto: UserDto) =
        usersRepository.saveAndFlush(
            userDto.toEntity()
        ).toDto()

    fun deleteById(userId: Long) = usersRepository.deleteById(userId)
}
