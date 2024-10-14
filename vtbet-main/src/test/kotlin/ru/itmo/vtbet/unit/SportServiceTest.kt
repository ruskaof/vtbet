package ru.itmo.vtbet.unit


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository
import ru.itmo.vtbet.repository.AvailableBetRepository
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

    private val sportRepository = mock(SportRepository::class.java)
    private val matchesRepository = mock(MatchesRepository::class.java)
    private val typeOfBetMatchRepository = mock(AvailableBetRepository::class.java)
    private val sportService = SportService(sportRepository, matchesRepository, typeOfBetMatchRepository)

    @Test
    fun `get sports`() {
        val sportId = 1L
        val sportName = "football"
        val sport2Id = 1L
        val sport2Name = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(SportEntity(sportId, sportName), SportEntity(sport2Id, sport2Name)))

        `when`(sportRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        val result = sportService.getSports(pageNumber, pageSize)
        val expectedResult = PagingDto(
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
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"
        val pageSize = 20
        val pageNumber = 1
        val pageMatches = PageImpl(listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)))

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
        val typeOfBetMatch = PageImpl(listOf(AvailableBetEntity(matchId, ratioNow, MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true),  TypeOfBetEntity(typeOfBetMatchId, description, BetGroupEntity(1L)))))

        `when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))).thenReturn((typeOfBetMatch))

        val result = sportService.getTypeOfBetMatch(matchId, pageNumber, pageSize)
        val expectedResult = PagingDto(
                items = listOf(SimpleTypeOfBetMatchDto(matchId, ratioNow, TypeOfBetDto(typeOfBetMatchId, description), matchId)),
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

        `when`(sportRepository.save(any())).thenReturn(SportEntity(1L, "football"))

        sportService.createSport(request)

        verify(sportRepository).save(
                SportEntity(null, "football")
        )
    }

    @Test
    fun `create match`() {
        val request = CreateMatchRequestDto(
                name = "El clasico",
        )

        `when`(matchesRepository.save(any())).thenReturn(MatchesEntity(1L, "El clasico", SportEntity(1L, "football"), false))
        `when`(sportRepository.findById(any())).thenReturn(Optional.of(SportEntity(1L, "football")))
        sportService.createMatch(request, 1L)

        verify(matchesRepository).save(
                MatchesEntity(null, "El clasico", SportEntity(1L, "football"), false)
        )
    }

}
