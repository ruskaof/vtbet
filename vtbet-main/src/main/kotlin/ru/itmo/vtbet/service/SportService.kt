package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.repository.SportRepository
import kotlin.jvm.optionals.getOrNull

@Service
class SportService(
        private val sportRepository: SportRepository,
) {

    fun getSports(): SportsDto {
        val usersEntity = usersRepository.findById(id).getOrNull()

        if (usersEntity == null || userAccountEntity == null) {
            throw ResourceNotFoundException("User with id $id not found")
        }

        return SportsDto(

        )
    }
}