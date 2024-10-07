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
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.dto.toDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
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
        val listSports = listOf(SportEntity(sportId, sportName), SportEntity(sport2Id, sport2Name))

        `when`(sportRepository.findAll()).thenReturn(listSports)

        // when
        val result = sportService.getSports(pageNumber, pageSize)

        // then
        val expectedResult = listOf(
                SportDto(
                        id = sportId,
                        name = sportName,
                ),
                SportDto(
                        id = sport2Id,
                        name = sport2Name,
                ),
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
        val pageMatches = PageImpl<MatchesEntity>(listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName),true)))

        `when`(matchesRepository.findBySportEntity_SportId(sportId, PageRequest.of(pageNumber, pageSize))).thenReturn((pageMatches))

        // when
        val result = sportService.getMatches(sportId, pageNumber, pageSize)

        // then
        val expectedResult = PagingDto(
                items = listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName),true)),
                total = 1L,
                pageSize = pageSize,
                page = pageNumber,
        )

        Assertions.assertEquals(expectedResult, result)
    }

}