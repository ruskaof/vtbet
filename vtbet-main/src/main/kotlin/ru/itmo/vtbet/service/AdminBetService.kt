package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.TypeOfBetMatchDto
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.model.request.CreateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.request.UpdateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.response.SimpleTypeOfBetMatchResponse
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import kotlin.jvm.optionals.getOrElse

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
@Service
class AdminBetService(
    private val betGroupRepository: BetGroupRepository,
    private val typeOfBetRepository: TypeOfBetRepository,
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository,
    private val matchesRepository: MatchesRepository,
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

    @Transactional
    fun updateBetMatch(
        id: Long,
        updateTypeOfBetMatchRequest: UpdateTypeOfBetMatchRequest
    ): SimpleTypeOfBetMatchResponse {
        val typeOfBetMatch =
            typeOfBetMatchRepository.findById(id).orElseThrow { ResourceNotFoundException("Invalid id: $id") }
        val newTypeOfBetMatch = typeOfBetMatch.copy(
            ratioNow = updateTypeOfBetMatchRequest.ratio.toBigDecimal(),
        )
        return typeOfBetMatchRepository.save(newTypeOfBetMatch).toResponse()
    }

    @Transactional
    fun createTypeOfBetMatch(
        createTypeOfBetMatchRequest: CreateTypeOfBetMatchRequest,
        matchId: Long
    ): TypeOfBetMatchDto {
        val typeOfBetEntity = typeOfBetRepository.findById(createTypeOfBetMatchRequest.typeOfBetId).getOrElse {
            throw ResourceNotFoundException("cannot find type of bet with id: ${createTypeOfBetMatchRequest.typeOfBetId}")
        }
        val matchesEntity = matchesRepository.findById(matchId).getOrElse {
            throw ResourceNotFoundException("cannot find match with id: $matchId")
        }
        val typeOfBetMatchEntity = typeOfBetMatchRepository.save(
            TypeOfBetMatchEntity(
                ratioNow = createTypeOfBetMatchRequest.ratioNow,
                match = matchesEntity,
                typeOfBets = typeOfBetEntity
            )
        )

        return TypeOfBetMatchDto(
            id = typeOfBetMatchEntity.typeOfBetMatchId!!,
            ratioNow = typeOfBetMatchEntity.ratioNow,
            typeOfBet = typeOfBetEntity.toDto(),
            match = matchesEntity.toDto()
        )
    }
}
