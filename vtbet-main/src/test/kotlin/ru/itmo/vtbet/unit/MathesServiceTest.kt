package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.request.UpdateMatchRequest
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.service.MatchesService
import java.math.BigDecimal
import java.util.*

class MathesServiceTest {

    private val matchesRepository = Mockito.mock(MatchesRepository::class.java)
    private val betsRepository = Mockito.mock(BetsRepository::class.java)
    private val userAccountRepository = Mockito.mock(UserAccountRepository::class.java)
    private val typeOfBetMatchRepository = Mockito.mock(TypeOfBetMatchRepository::class.java)
    private val matchesService = MatchesService(matchesRepository, betsRepository, userAccountRepository, typeOfBetMatchRepository)

    @Test
    fun `get matches`() {
        // given
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl<MatchesEntity>(listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        // when
        val result = matchesService.getMatches(pageNumber, pageSize)

        // then
        val expectedResult = PagingDto<MatchDto>(
                listOf(
                        MatchDto(
                                id = sportId,
                                name = matchName,
                                SportDto(
                                        id = sportId,
                                        name = sportName,
                                ),
                                true
                        )
                ),
                1,
                pageNumber,
                pageSize
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get bets by match id`() {
        val typeOfBetMatchId = 1L
        val description = "team 1 win"
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val ratioNow = BigDecimal(2)
        val pageSports = PageImpl<TypeOfBetMatchEntity>(listOf(TypeOfBetMatchEntity(matchId, ratioNow, MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true), TypeOfBetEntity(typeOfBetMatchId, description, BetGroupEntity(1L)))))

        Mockito.`when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        // when
        val result = matchesService.getBetsByMatchId(matchId, pageNumber, pageSize)

        // then
        val expectedResult = PagingDto<SimpleTypeOfBetMatchDto>(
                listOf(
                        SimpleTypeOfBetMatchDto(
                                id = sportId,
                                ratioNow = ratioNow,
                                TypeOfBetDto(
                                        id = sportId,
                                        description = description,
                                ),
                                matchId
                        )
                ),
                1,
                pageNumber,
                pageSize
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `update match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val name = "42"
        val matchesEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)
        val matches2Entity = MatchesEntity(matchId, name, SportEntity(sportId, sportName), true)

        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchesEntity))

        Mockito.`when`(matchesRepository.save(matches2Entity)).thenReturn(matches2Entity)
        // when
        val result = matchesService.updateMatch(UpdateMatchRequest(name), matchId)

        // then
        val expectedResult = MatchDto(
                matchId,
                name,
                SportDto(matchId, sportName),
                true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `end match`() {

    }

    @Test
    fun `delete match`() {
        // given
        val matchId = 1L


        Mockito.`when`(matchesRepository.existsById(matchId)).thenReturn(true)

        // when
        val result = matchesService.delete(matchId)

        Mockito.verify(matchesRepository).deleteById(
                matchId
        )
    }
}