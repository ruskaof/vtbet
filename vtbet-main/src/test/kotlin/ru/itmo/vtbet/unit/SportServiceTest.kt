package ru.itmo.vtbet.unit


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportsRepository
import ru.itmo.vtbet.repository.AvailableBetsRepository
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.entity.*
import ru.itmo.vtbet.model.request.CreateMatchRequestDto
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import java.math.BigDecimal
import java.util.*

class SportServiceTest {

    private val sportsRepository = mock(SportsRepository::class.java)
    private val matchesRepository = mock(MatchesRepository::class.java)
    private val typeOfBetMatchRepository = mock(AvailableBetsRepository::class.java)
    private val sportService = SportService(sportsRepository, matchesRepository, typeOfBetMatchRepository)

    @Test
    fun `get sports`() {
        val sportId = 1L
        val sportName = "football"
        val sport2Id = 1L
        val sport2Name = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(SportsEntity(sportId, sportName), SportsEntity(sport2Id, sport2Name)))

        `when`(sportsRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        val result = sportService.getSports(pageNumber, pageSize)
        val expectedResult = PagingDto(
                listOf(
                        SportDto(
                                sportId = sportId,
                                name = sportName,
                        ),
                        SportDto(
                                sportId = sport2Id,
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
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"
        val pageSize = 20
        val pageNumber = 1
        val pageMatches = PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)))

        `when`(matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize))).thenReturn((pageMatches))

        val result = sportService.getMatches(sportId, pageNumber, pageSize)
        val expectedResult = PagingDto(
                items = listOf(MatchDto(matchId, matchName, SportDto(sportId, sportName), true)),
                total = 1L,
                pageSize = pageSize,
                page = pageNumber,
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get TypeOfBetMatch`() {
        val typeOfBetMatchId = 1L
        val description = "team 1 win"
        val ratioNow = BigDecimal(2)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"
        val pageSize = 20
        val pageNumber = 1
        val typeOfBetMatch = PageImpl(listOf(AvailableBetsEntity(matchId, ratioNow, MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true),  TypeOfBetEntity(typeOfBetMatchId, description, BetsGroupsEntity(1L)))))

        `when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))).thenReturn((typeOfBetMatch))

        val result = sportService.getTypeOfBetMatch(matchId, pageNumber, pageSize)
        val expectedResult = PagingDto(
                items = listOf(SimpleAvailableBetsDto(matchId, ratioNow, BetGroup(typeOfBetMatchId, description), matchId)),
                total = 1L,
                pageSize = pageSize,
                page = pageNumber,
        )

        Assertions.assertEquals(expectedResult, result)
    }


    @Test
    fun `create sport`() {
        val request = CreateSportRequestDto(
                name = "football",
        )

        `when`(sportsRepository.save(any())).thenReturn(SportsEntity(1L, "football"))

        sportService.createSport(request)

        verify(sportsRepository).save(
                SportsEntity(null, "football")
        )
    }

    @Test
    fun `create match`() {
        val request = CreateMatchRequestDto(
                name = "El clasico",
        )

        `when`(matchesRepository.save(any())).thenReturn(MatchesEntity(1L, "El clasico", SportsEntity(1L, "football"), false))
        `when`(sportsRepository.findById(any())).thenReturn(Optional.of(SportsEntity(1L, "football")))
        sportService.createMatch(request, 1L)

        verify(matchesRepository).save(
                MatchesEntity(null, "El clasico", SportsEntity(1L, "football"), false)
        )
    }

}
