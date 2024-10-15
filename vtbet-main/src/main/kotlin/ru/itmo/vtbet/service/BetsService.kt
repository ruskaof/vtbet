package ru.itmo.vtbet.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.request.CreateBetsGroupsRequestDto
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import java.math.BigDecimal

@Service
class BetsService(
    private val betsRepository: BetsRepository,
    private val betsGroupsRepository: BetsGroupsRepository,
) {
    @Transactional
    fun getBetGroup(groupId: Long) =
        betsGroupsRepository.findByIdOrNull(groupId)?.toDto()

    @Transactional
    fun createBetGroup(createBetsGroupsRequestDto: CreateBetsGroupsRequestDto) =
        createBetsGroupsRequestDto.groups.map { group ->
            betsGroupsRepository.save(BetsGroupsEntity(description = group.description)).toDto()
        }

    fun getBetsByAvailableBetIds(availableBetIds: List<Long>) =
        betsRepository.findAllByAvailableBetIdIn(availableBetIds).map(BetsEntity::toDto)

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
        ).toDto()
    }
}