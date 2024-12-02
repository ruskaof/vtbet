package ru.itmo.bets.handler.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.bets.handler.kafka.KafkaProducer
import ru.itmo.bets.handler.repository.BetsGroupsRepository
import ru.itmo.bets.handler.repository.BetsRepository
import ru.itmo.bets.handler.request.CreateBetsGroupsRequestDto
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.bets.handler.model.entity.BetsEntity
import ru.itmo.bets.handler.model.entity.BetsGroupsEntity
import ru.itmo.common.exception.ResourceNotFoundException
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

@Service
class BetsService(
    private val betsRepository: BetsRepository,
    private val betsGroupsRepository: BetsGroupsRepository,
) {
    @Transactional
    fun getBetGroup(groupId: Long) = betsGroupsRepository.findByIdOrNull(groupId)?.toDto()

    @Transactional
    fun createBetGroup(createBetsGroupsRequestDto: CreateBetsGroupsRequestDto) =
        createBetsGroupsRequestDto.groups.map { group ->
            betsGroupsRepository.save(BetsGroupsEntity(description = group.description)).toDto()
        }

    @Transactional
    fun deleteBetGroup(betGroupId: Long) {
        getBetGroup(betGroupId)
            ?: throw ResourceNotFoundException("Bet group with id $betGroupId not found")
        delete(betGroupId)
    }

    @Transactional
    fun getBetGroups(pageNumber: Int, pageSize: Int): PagingDto<BetGroupDto> {
        val result = betsGroupsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("groupId")))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getBetsByAvailableBetIds(availableBetIds: List<Long>) =
        betsRepository.findAllByAvailableBetIdIn(availableBetIds).map(BetsEntity::toDto)

    fun createBet(
        userId: Long,
        availableBetDto: AvailableBetDto,
        ratio: BigDecimal,
        amount: BigDecimal,
    ): BetDto {
        return betsRepository.save(
            BetsEntity(
                amount = amount,
                ratio = ratio,
                userId = userId,
                availableBetId = availableBetDto.availableBetId,
            )
        ).toDto()
    }

    private fun delete(betGroupId: Long) {
        betsGroupsRepository.findById(betGroupId).getOrNull()
            ?: throw ResourceNotFoundException("Bet group with id $betGroupId not found")
        betsGroupsRepository.deleteById(betGroupId)
    }

    fun getUserBets(userId: Long, pageNumber: Int, pageSize: Int): PagingDto<BetDto> {
        val result = betsRepository.findAllByUserId(userId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}