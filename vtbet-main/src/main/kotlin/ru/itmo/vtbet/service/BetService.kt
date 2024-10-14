package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.AvailableBetEntity
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.CreateBetGroupRequestDto
import ru.itmo.vtbet.repository.AvailableBetRepository
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

@Service
class BetService(
    private val betsRepository: BetsRepository,
    private val betGroupRepository: BetGroupRepository,
    private val typeOfBetRepository: TypeOfBetRepository,
    private val availableBetRepository: AvailableBetRepository,
) {

    fun createTypeOfBet(
        betGroupDto: BetGroupDto,
        typeOfBetDto: TypeOfBetDto,
    ) = typeOfBetRepository.save(
        TypeOfBetEntity(
            description = typeOfBetDto.description,
            betGroupId = betGroupDto.id,
        )
    )

    @Transactional
    fun createBetGroup(createBetGroupRequestDto: CreateBetGroupRequestDto): BetGroupDto {
        val betGroupEntity = betGroupRepository.save(BetGroupEntity())
        val typeOfBets = createBetGroupRequestDto.typeOfBets.map {
            typeOfBetRepository.save(
                TypeOfBetEntity(
                    description = it.description,
                    betGroupId = betGroupEntity.betGroupId!!,
                )
            )
        }

        return BetGroupDto(
            id = betGroupEntity.betGroupId!!,
            typeOfBets = typeOfBets.map(TypeOfBetEntity::toDto)
        )
    }

    @Transactional
    fun createAvailableBet(
        createAvailableBetRequestDto: CreateAvailableBetRequestDto,
        matchDto: MatchDto,
        typeOfBetDto: TypeOfBetDto,
    ): AvailableBetDto {
        return availableBetRepository.save(
            AvailableBetEntity(
                ratioNow = createAvailableBetRequestDto.ratioNow,
                matchId = matchDto.id,
                typeOfBetId = typeOfBetDto.id,
                betsClosed = false,
            )
        ).toDto()
    }

    fun getAvailableBet(id: Long): AvailableBetDto? =
        availableBetRepository.findById(id).getOrNull()?.toDto()

    fun getMatchAvailableBets(matchId: Long): List<AvailableBetDto> =
        availableBetRepository.findAllByMatchMatchId(matchId).map(AvailableBetEntity::toDto)

    fun getMatchBets(matchId: Long): List<BetDto> =
        betsRepository.findAllByMatchMatchId(matchId).map(BetsEntity::toDto)

    @Transactional
    fun closeAvailableBet(id: Long) {
        val availableBet = availableBetRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Available bet with id $id not found")
        }
        availableBetRepository.save(availableBet.copy(betsClosed = true))
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
                usersEntity = userDto.toUsersEntity(),
                availableBetId = availableBetDto.id,
            )
        )
            .toDto()
    }
}