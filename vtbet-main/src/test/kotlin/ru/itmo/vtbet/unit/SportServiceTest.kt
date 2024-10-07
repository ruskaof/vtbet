package ru.itmo.vtbet.unit


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import org.mockito.Mockito.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.request.CreateMatchRequest
import ru.itmo.vtbet.model.request.CreateSportRequest
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.service.SportService
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.collections.List

class SportServiceTest {

    private val sportRepository = mock(SportRepository::class.java)
    private val matchesRepository = mock(MatchesRepository::class.java)
    private val typeOfBetMatchRepository = mock(TypeOfBetMatchRepository::class.java)
    private val sportService = SportService(sportRepository, matchesRepository, typeOfBetMatchRepository)

    @Test
    fun `get sports`() {
        // given
        val sportId = 1L
        val sportName = "football"
        val sport2Id = 1L
        val sport2Name = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl<SportEntity>(listOf(SportEntity(sportId, sportName), SportEntity(sport2Id, sport2Name)))

        `when`(sportRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        // when
        val result = sportService.getSports(pageNumber, pageSize)

        // then
        val expectedResult = PagingDto<SportDto>(
                listOf(
                        SportDto(
                                id = sportId,
                                name = sportName,
                        ),
                        SportDto(
                                id = sport2Id,
                                name = sport2Name,
                        )

                ),
                2,
                pageNumber,
                pageSize
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get mathes`() {
        // given
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"
        val pageSize = 20
        val pageNumber = 1
        val pageMatches = PageImpl<MatchesEntity>(listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)))

        `when`(matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize))).thenReturn((pageMatches))

        // when
        val result = sportService.getMatches(sportId, pageNumber, pageSize)

        // then
        val expectedResult = PagingDto<MatchDto>(
                items = listOf(MatchDto(matchId, matchName, SportDto(sportId, sportName), true)),
                total = 1L,
                pageSize = pageSize,
                page = pageNumber,
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get TypeOfBetMatch`() {
        // given
        val typeOfBetMatchId = 1L
        val description = "team 1 win"
        val ratioNow = BigDecimal(2)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"
        val pageSize = 20
        val pageNumber = 1
        val TypeOfBetMatch = PageImpl<TypeOfBetMatchEntity>(listOf(TypeOfBetMatchEntity(matchId, ratioNow, MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true),  TypeOfBetEntity(typeOfBetMatchId, description, BetGroupEntity(1L)))))

        `when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))).thenReturn((TypeOfBetMatch))

        // when
        val result = sportService.getTypeOfBetMatch(matchId, pageNumber, pageSize)

        // then
        val expectedResult = PagingDto<SimpleTypeOfBetMatchDto>(
                items = listOf(SimpleTypeOfBetMatchDto(matchId, ratioNow, TypeOfBetDto(typeOfBetMatchId, description), matchId)),
                total = 1L,
                pageSize = pageSize,
                page = pageNumber,
        )

        Assertions.assertEquals(expectedResult, result)
    }


    @Test
    fun `create sport`() {
        // given
        val request = CreateSportRequest(
                name = "football",
        )

        `when`(sportRepository.save(any())).thenReturn(SportEntity(1L, "football"))

        // when
        sportService.createSport(request)

        // then
        verify(sportRepository).save(
                SportEntity(null, "football")
        )
    }

    @Test
    fun `create match`() {
        // given
        val request = CreateMatchRequest(
                name = "El clasico",
        )

        `when`(matchesRepository.save(any())).thenReturn(MatchesEntity(1L, "El clasico", SportEntity(1L, "football"), false))
        `when`(sportRepository.findById(any())).thenReturn(Optional.of(SportEntity(1L, "football")))
        // when
        sportService.createMatch(request, 1L)

        // then
        verify(matchesRepository).save(
                MatchesEntity(null, "El clasico", SportEntity(1L, "football"), false)
        )
    }

}