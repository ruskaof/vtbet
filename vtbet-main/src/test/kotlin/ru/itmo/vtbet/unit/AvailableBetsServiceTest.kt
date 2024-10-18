package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.service.AvailableBetsService
import ru.itmo.vtbet.service.BetsService
import ru.itmo.vtbet.service.toDto
import ru.itmo.vtbet.service.toDtoWithBetGroup
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class AvailableBetsServiceTest {


    private val availableBetsRepository = Mockito.mock(AvailableBetsRepository::class.java)

    private val availableBetsService = AvailableBetsService(availableBetsRepository)

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
        Mockito.`when`(availableBetsRepository.findAllByMatchId(matchId, PageRequest.of(pageNumber, pageSize))).thenReturn(PageImpl(listOf( availableBetsEntity)))

        val result = availableBetsService.getAllByMatchId(matchId, pageNumber, pageSize)

        Assertions.assertEquals(listAvailableBetDto, result)
    }


}