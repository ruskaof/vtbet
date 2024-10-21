package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.service.*
import java.math.BigDecimal
import java.util.*

class AvailableBetsServiceTest {


    private val availableBetsRepository = Mockito.mock(AvailableBetsRepository::class.java)
    private val matchesOperationsService = Mockito.mock(MatchesOperationsService::class.java)

    private val availableBetsService = AvailableBetsService(availableBetsRepository, matchesOperationsService)

    @Test
    fun `save`() {
        val matchId = 1L
        val gruopId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = gruopId,
                description = description,
            )
        )

        val availableBetWithBetGroupDto = availableBetsEntity.toDtoWithBetGroup()

        Mockito.`when`(availableBetsRepository.saveAndFlush(Mockito.any())).thenReturn(availableBetsEntity)

        val result = availableBetsService.save(availableBetWithBetGroupDto)

        Assertions.assertEquals(availableBetWithBetGroupDto, result)
    }

    @Test
    fun `update`() {
        val matchId = 1L
        val gruopId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = gruopId,
                description = description,
            )
        )

        val availableBetWithBetGroupDto = availableBetsEntity.toDtoWithBetGroup()
        val availableBetDto = availableBetsEntity.toDto()
        Mockito.`when`(availableBetsRepository.saveAndFlush(Mockito.any())).thenReturn(availableBetsEntity)

        val result = availableBetsService.update(availableBetWithBetGroupDto)

        Assertions.assertEquals(availableBetDto, result)
    }

    @Test
    fun `get all by match id`() {
        val matchId = 1L
        val gruopId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = gruopId,
                description = description,
            )
        )

        val listAvailableBetDto = listOf( availableBetsEntity.toDto())
        Mockito.`when`(availableBetsRepository.findAllByMatchId(matchId)).thenReturn(listOf( availableBetsEntity))

        val result = availableBetsService.getAllByMatchId(matchId)

        Assertions.assertEquals(listAvailableBetDto, result)
    }

    @Test
    fun `get available bet`() {
        val matchId = 1L
        val gruopId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = gruopId,
                description = description,
            )
        )

        val availableBetDto = availableBetsEntity.toDto()
        Mockito.`when`(availableBetsRepository.findById(matchId)).thenReturn(Optional.of(availableBetsEntity))

        val result = availableBetsService.getAvailableBet(matchId)

        Assertions.assertEquals(availableBetDto, result)
    }

    @Test
    fun `get available bet with bet group`() {
        val matchId = 1L
        val groupId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = groupId,
                description = description,
            )
        )

        val availableBetDto = availableBetsEntity.toDtoWithBetGroup()
        Mockito.`when`(availableBetsRepository.findById(matchId)).thenReturn(Optional.of(availableBetsEntity))

        val result = availableBetsService.getAvailableBetWithGroup(matchId)

        Assertions.assertEquals(availableBetDto, result)
    }

    @Test
    fun `get all by match id page`() {
        val total = 1L
        val pageSize = 1
        val pageNumber = 1
        val matchId = 1L
        val gruopId = 1L
        val availableBetId = 1L
        val ratio = BigDecimal(1.5)
        val betsClosed = false
        val description = "description"

        val availableBetsEntity = AvailableBetsEntity(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = betsClosed,
            matchId = matchId,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = gruopId,
                description = description,
            )
        )

        val listAvailableBetDto = PagingDto<AvailableBetDto>(listOf( availableBetsEntity.toDto()), total, pageNumber, pageSize)
        Mockito.`when`(matchesOperationsService.getMatch(matchId)).thenReturn(MatchDto(1, "test", SportDto(1, "test"), false))
        Mockito.`when`(availableBetsRepository.findAllByMatchId(matchId, PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))).thenReturn(PageImpl(listOf( availableBetsEntity)))

        val result = availableBetsService.getAllByMatchId(matchId, pageNumber, pageSize)

        Assertions.assertEquals(listAvailableBetDto, result)
    }


}