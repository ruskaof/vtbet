package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.CreateBetGroupRequestDto
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

@Service
class BetsService(
    private val betsRepository: BetsRepository,
    private val betsGroupsRepository: BetsGroupsRepository,
    private val typeOfBetRepository: TypeOfBetRepository,
    private val availableBetsRepository: AvailableBetsRepository,
) {

    fun createTypeOfBet(
        betGroupDto: BetGroupDto,
        betGroup: BetGroup,
    ) = typeOfBetRepository.save(
        TypeOfBetEntity(
            description = betGroup.description,
            betGroupId = betGroupDto.groupId,
        )
    )

    @Transactional
    fun createBetGroup(createBetGroupRequestDto: CreateBetGroupRequestDto): BetGroupDto {
        val betsGroupsEntity = betsGroupsRepository.save(BetsGroupsEntity())
        val typeOfBets = createBetGroupRequestDto.typeOfBets.map {
            typeOfBetRepository.save(
                TypeOfBetEntity(
                    description = it.description,
                    betGroupId = betsGroupsEntity.betGroupId!!,
                )
            )
        }

        return BetGroupDto(
            groupId = betsGroupsEntity.betGroupId!!,
            typeOfBets = typeOfBets.map(TypeOfBetEntity::toDto)
        )
    }

    @Transactional
    fun createAvailableBet(
        createAvailableBetRequestDto: CreateAvailableBetRequestDto,
        matchDto: MatchDto,
        betGroup: BetGroup,
    ): AvailableBetDto {
        return availableBetsRepository.save(
            AvailableBetsEntity(
                ratioNow = createAvailableBetRequestDto.ratioNow,
                matchId = matchDto.matchId,
                typeOfBetId = betGroup.id,
                betsClosed = false,
            )
        ).toDto()
    }

    fun getAvailableBet(id: Long): AvailableBetDto? =
        availableBetsRepository.findById(id).getOrNull()?.toDto()

    fun getMatchAvailableBets(matchId: Long): List<AvailableBetDto> =
        availableBetsRepository.findAllByMatchMatchId(matchId).map(AvailableBetsEntity::toDto)

    fun getMatchBets(matchId: Long): List<BetDto> =
        betsRepository.findAllByMatchMatchId(matchId).map(BetsEntity::toDto)

    @Transactional
    fun closeAvailableBet(id: Long) {
        val availableBet = availableBetsRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Available bet with id $id not found")
        }
        availableBetsRepository.save(availableBet.copy(betsClosed = true))
    }

    fun createBet(
        userDto: UserDto,
        availableBetDto: AvailableBetDto,
        ratio: BigDecimal,
        amount: BigDecimal,
    ): BetDto {
        return betsRepository.save(
            BetsEntity(
                amount = amount,
                ratio = ratio,
                usersEntity = userDto.toEntity(),
                availableBetId = availableBetDto.availableBetId,
            )
        )
            .toDto()
    }
}