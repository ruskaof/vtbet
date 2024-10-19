package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.request.CreateBetsGroupsRequestDto
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

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

    fun getBetGroups(pageNumber: Int, pageSize: Int): PagingDto<BetGroupDto> {
        val result = betsGroupsRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun delete(betGroupId: Long) {
        betsGroupsRepository.findById(betGroupId).getOrNull()
            ?: throw ResourceNotFoundException("Bet group with id $betGroupId not found")
        betsGroupsRepository.deleteById(betGroupId)
    }

    fun getUserBets(userId: Long, pageNumber: Int, pageSize: Int): PagingDto<BetDto> {
        val result = betsRepository.findAllByUsersEntityUserId(userId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}