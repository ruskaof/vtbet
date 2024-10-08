package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.model.request.CreateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.request.TypeOfBetRequest
import ru.itmo.vtbet.model.request.UpdateTypeOfBetMatchRequest
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import ru.itmo.vtbet.service.AdminBetService
import ru.itmo.vtbet.service.toDto
import java.math.BigDecimal
import java.util.Optional

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

    @Test
    fun `updateBetMatch updates successful when valid id`() {
        val id = 2L
        val ratio = BigDecimal(2)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), false)
        val typeOfBet = TypeOfBetEntity(
            typeOfBetId = 1,
            description = "",
            betGroupEntity = BetGroupEntity(
                betGroupId = 1,
            ),
        )
        val newRatio = 1.1
        val request = UpdateTypeOfBetMatchRequest(newRatio)
        val oldTypeOfBetMatch =
            TypeOfBetMatchEntity(typeOfBetMatchId = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)

        Mockito.`when`(typeOfBetMatchRepository.findById(id)).thenReturn(Optional.of(oldTypeOfBetMatch))
        Mockito.`when`(typeOfBetMatchRepository.save(any()))
            .thenReturn(oldTypeOfBetMatch.copy(ratioNow = newRatio.toBigDecimal()))

        adminBetService.updateBetMatch(id, request)

        verify(typeOfBetMatchRepository).findById(id)
        verify(typeOfBetMatchRepository).save(any())
    }

    @Test
    fun `createTypeOfBetMatch creates bet match successfully`() {
        val id = 2L
        val ratio = BigDecimal(2)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), false)
        val typeOfBet = TypeOfBetEntity(
            typeOfBetId = 1,
            description = "",
            betGroupEntity = BetGroupEntity(
                betGroupId = 1,
            ),
        )
        val typeOfBetMatch = TypeOfBetMatchEntity(typeOfBetMatchId = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
        val typeOfBetId = 1L
        val createRequest = CreateTypeOfBetMatchRequest(typeOfBetId, 1.1.toBigDecimal())

        Mockito.`when`(typeOfBetRepository.findById(typeOfBetId)).thenReturn(Optional.of(typeOfBet))
        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
        Mockito.`when`(typeOfBetMatchRepository.save(any())).thenReturn(typeOfBetMatch)

        val createdTypeOfBetMatch = adminBetService.createTypeOfBetMatch(createRequest, matchId)

        verify(typeOfBetRepository).findById(typeOfBetId)
        verify(matchesRepository).findById(matchId)
        verify(typeOfBetMatchRepository).save(any())

        assertEquals(createdTypeOfBetMatch.id, typeOfBetMatch.typeOfBetMatchId)
        assertEquals(createdTypeOfBetMatch.ratioNow, typeOfBetMatch.ratioNow)
    }
}
