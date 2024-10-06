package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.toDto
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
@Service
class AdminBetService(
    private val betGroupRepository: BetGroupRepository,
    private val typeOfBetRepository: TypeOfBetRepository,
) {

    @Transactional
    fun createBetGroup(createBetGroupRequest: CreateBetGroupRequest): BetGroupDto {
        val betGroupEntity = betGroupRepository.save(BetGroupEntity())
        val typeOfBets = createBetGroupRequest.typeOfBets.map {
            typeOfBetRepository.save(
                TypeOfBetEntity(
                    description = it.description,
                    betGroupEntity = betGroupEntity
                )
            )
        }

        return BetGroupDto(
            id = betGroupEntity.betGroupId!!,
            typeOfBets = typeOfBets.map(TypeOfBetEntity::toDto)
        )
    }
}