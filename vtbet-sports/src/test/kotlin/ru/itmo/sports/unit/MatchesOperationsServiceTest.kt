package ru.itmo.sports.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.entity.MatchesEntity
import ru.itmo.common.dto.MatchDto
import ru.itmo.common.dto.SportDto
import ru.itmo.common.entity.SportsEntity
import ru.itmo.sports.repository.MatchesRepository
import ru.itmo.sports.service.MatchesOperationsService
import java.util.*

class MatchesOperationsServiceTest {
    private val matchesRepository = Mockito.mock(MatchesRepository::class.java)

    private val matchesOperationsService =
        MatchesOperationsService(matchesRepository)

    @Test
    fun `get matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        val result = matchesOperationsService.getMatches(PageRequest.of(pageNumber, pageSize))
        val expectedResult = listOf(
            MatchDto(
                matchId = sportId,
                name = matchName,
                SportDto(
                    sportId = sportId,
                    name = sportName,
                ),
                true
            )
        )
        Assertions.assertEquals(expectedResult, result.content)
    }

    @Test
    fun `get matches by sportId`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize)))
            .thenReturn(pageSports)

        val result = matchesOperationsService.getMatches(sportId, PageRequest.of(pageNumber, pageSize))
        val expectedResult = listOf(
            MatchDto(
                matchId = sportId,
                name = matchName,
                SportDto(
                    sportId = sportId,
                    name = sportName,
                ),
                true
            )
        )
        Assertions.assertEquals(expectedResult, result.content)
    }

    @Test
    fun `save match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val name = "42"
        val ended = false
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)

        val matchDto = MatchDto(
            matchId = matchId,
            name = name,
            sport = SportDto(
                sportId = sportId,
                name = sportName,
            ),
            ended = ended,
        )

        Mockito.`when`(matchesRepository.saveAndFlush(any())).thenReturn(matchesEntity)

        val result = matchesOperationsService.save(matchDto)


        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
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
        val ended = true

        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)

        val matchDto = MatchDto(
            matchId = matchId,
            name = name,
            sport = SportDto(
                sportId = sportId,
                name = sportName,
            ),
            ended = ended,
        )

        Mockito.`when`(matchesRepository.saveAndFlush(any())).thenReturn(matchesEntity)


        val result = matchesOperationsService.update(matchDto)

        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)


        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.of(matchesEntity))

        val result = matchesOperationsService.getMatch(matchId)


        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `delete match`() {
        val matchId = 1L

        matchesOperationsService.delete(matchId)

        verify(matchesRepository).deleteById(any())
    }

    @Test
    fun `end match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false)

        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.of(matchesEntity))


        matchesOperationsService.endMatch(matchId)


        verify(matchesRepository).save(matchesEntity.copy(ended = true))
    }

    @Test
    fun `end match with exception`() {
        val matchId = 1L

        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> { matchesOperationsService.endMatch(matchId) }

    }

}
