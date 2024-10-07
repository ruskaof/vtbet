package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.model.request.TypeOfBetRequest
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import ru.itmo.vtbet.service.AdminBetService
import ru.itmo.vtbet.service.toDto

class AdminBetServiceTest {
    private val betGroupRepository = mock(BetGroupRepository::class.java)
    private val typeOfBetRepository = mock(TypeOfBetRepository::class.java)
    private val typeOfBetMatchRepository = mock(TypeOfBetMatchRepository::class.java)
    private val matchesRepository = mock(MatchesRepository::class.java)

    private val adminBetService =
        AdminBetService(betGroupRepository, typeOfBetRepository, typeOfBetMatchRepository, matchesRepository)

    @Test
    fun createBetGroup() {
        val betGroupId = 1L
        val betGroupEntity = BetGroupEntity(betGroupId)

        val createBetGroupRequest =
            CreateBetGroupRequest(listOf(TypeOfBetRequest("description1"), TypeOfBetRequest("description2")))
        val typeOfBets = listOf(
            TypeOfBetEntity(1L, "description1", betGroupEntity),
            TypeOfBetEntity(1L, "description2", betGroupEntity),
        )

        val expectedBetGroupDto = BetGroupDto(
            betGroupId,
            typeOfBets.map(TypeOfBetEntity::toDto)
        )

        Mockito.`when`(betGroupRepository.save(any(BetGroupEntity::class.java))).thenReturn(betGroupEntity)
        Mockito.`when`(typeOfBetRepository.save(any(TypeOfBetEntity::class.java))).thenReturn(
            typeOfBets[0],
            typeOfBets[1],
        )

        val betGroupDto = adminBetService.createBetGroup(createBetGroupRequest)

        assertEquals(expectedBetGroupDto, betGroupDto)

        val inOrder = inOrder(betGroupRepository, typeOfBetRepository)
        inOrder.verify(betGroupRepository).save(any(BetGroupEntity::class.java))
        inOrder.verify(typeOfBetRepository, times(2)).save(any(TypeOfBetEntity::class.java))
    }
}
