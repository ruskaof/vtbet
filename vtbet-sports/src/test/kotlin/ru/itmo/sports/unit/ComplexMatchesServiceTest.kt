package ru.itmo.sports.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.CreateMatchRequestDto
import ru.itmo.common.request.UpdateMatchRequestDto
import ru.itmo.common.response.MatchResponse
import ru.itmo.common.response.SportResponse
import ru.itmo.sports.model.dto.MatchDto
import ru.itmo.sports.model.dto.SportDto
import ru.itmo.sports.model.entity.MatchesEntity
import ru.itmo.sports.model.entity.SportsEntity
import ru.itmo.sports.service.ComplexMatchesService
import ru.itmo.sports.service.MatchesOperationsService
import ru.itmo.sports.service.SportsService
import ru.itmo.sports.service.toDto

class ComplexMatchesServiceTest {

    private val matchesOperationsService = Mockito.mock(MatchesOperationsService::class.java)

    private val sportsService = Mockito.mock(SportsService::class.java)

    private val complexMatchesService =
        ComplexMatchesService(matchesOperationsService, sportsService)

    @Test
    fun `get matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports =
            PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true).toDto()))

        Mockito.`when`(matchesOperationsService.getMatches(PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))).thenReturn(pageSports)

        val result = complexMatchesService.getMatches(pageNumber, pageSize)
        val expectedResult = PagingDto(
            listOf(
                MatchDto(
                    matchId = sportId,
                    name = matchName,
                    SportDto(
                        sportId = sportId,
                        name = sportName,
                    ),
                    true
                )
            ),
            1,
            1,
            pageSize,
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get matches with exception`() {
        val sportId = 1L
        val pageSize = 20
        val pageNumber = 1

        Mockito.`when`(sportsService.getSport(sportId)).thenReturn(null)

        val exception =
            assertThrows<ResourceNotFoundException> { complexMatchesService.getMatches(sportId, pageNumber, pageSize) }

        Assertions.assertEquals(exception.message, "Sport with id 1 not found")
    }


    @Test
    fun `get matches with sportId`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports =
            PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true).toDto()))

        Mockito.`when`(sportsService.getSport(sportId)).thenReturn(SportDto(sportId, sportName))
        Mockito.`when`(matchesOperationsService.getMatches(sportId, PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))).thenReturn(pageSports)

        val result = complexMatchesService.getMatches(sportId, pageNumber, pageSize)
        val expectedResult = PagingDto(
            listOf(
                MatchDto(
                    matchId = sportId,
                    name = matchName,
                    SportDto(
                        sportId = sportId,
                        name = sportName,
                    ),
                    true
                )
            ),
            1,
            1,
            pageSize,
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1

        Mockito.`when`(matchesOperationsService.getMatch(matchId))
            .thenReturn(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true).toDto())

        val result = complexMatchesService.getMatch(matchId)
        val expectedResult = MatchResponse(
            id = matchId,
            name = matchName,
            ended = true,
            sport = SportResponse(
                id = sportId,
                name = sportName,
            )
        )

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get match with exception`() {
        val matchId = 1L

        val exception = assertThrows<ResourceNotFoundException> {
            complexMatchesService.getMatch(
                matchId
            )
        }

        Assertions.assertEquals(exception.message, "Match with id $matchId not found")
    }

    @Test
    fun `update matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val matchName2 = "El Clasico2"
        val sportId = 1L
        val sportName = "football"
        val matchDto = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false).toDto()
        val updateMatchRequestDto = UpdateMatchRequestDto(
            matchName2
        )
        Mockito.`when`(matchesOperationsService.update(MockitoHelper.anyObject())).thenReturn(matchDto)
        Mockito.`when`(matchesOperationsService.getMatch(matchId)).thenReturn(matchDto)

        val result = complexMatchesService.updateMatch(updateMatchRequestDto, matchId)

        val expectedResult = matchDto.copy()

        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `update matches with exception`() {
        val matchId = 1L
        val matchName2 = "El Clasico2"
        val sportId = 1L
        val updateMatchRequestDto = UpdateMatchRequestDto(
            matchName2
        )

        Mockito.`when`(matchesOperationsService.getMatch(matchId)).thenReturn(null)

        val exception = assertThrows<ResourceNotFoundException> {
            complexMatchesService.updateMatch(
                updateMatchRequestDto,
                sportId
            )
        }

        Assertions.assertEquals(exception.message, "Invalid id: 1")
    }

    @Test
    fun `delete matches with exception`() {
        val matchId = 1L

        Mockito.`when`(matchesOperationsService.getMatch(matchId)).thenReturn(null)

        val exception = assertThrows<ResourceNotFoundException> { complexMatchesService.delete(matchId) }

        Assertions.assertEquals(exception.message, "Match with id 1 not found")
    }

    @Test
    fun `delete matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val matchDto = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false).toDto()

        Mockito.`when`(matchesOperationsService.getMatch(matchId)).thenReturn(matchDto)

        complexMatchesService.delete(matchId)

        verify(matchesOperationsService).delete(matchId)
    }

    @Test
    fun `create matches with exception`() {
        val matchId = 1L
        val sportId = 1L
        val matchName = "tratata"
        val createMatchRequestDto = CreateMatchRequestDto(
            sportId,
            matchName
        )
        Mockito.`when`(sportsService.getSport(matchId)).thenReturn(null)

        val exception =
            assertThrows<ResourceNotFoundException> { complexMatchesService.createMatch(createMatchRequestDto) }

        Assertions.assertEquals(exception.message, "Sport with id 1 not found")
    }

    @Test
    fun `create matches`() {
        val matchId = 1L
        val sportId = 1L
        val sportName = "football"
        val matchName = "tratata"
        val createMatchRequestDto = CreateMatchRequestDto(
            sportId,
            matchName
        )
        val sportDto = SportDto(
            sportId,
            sportName,
        )
        val matchDto = MatchDto(
            matchId,
            matchName,
            sportDto,
            false,
        )

        Mockito.`when`(sportsService.getSport(matchId)).thenReturn(sportDto)
        Mockito.`when`(matchesOperationsService.save(MockitoHelper.anyObject())).thenReturn(matchDto)

        val result =  complexMatchesService.createMatch(createMatchRequestDto)
        val expectedResult = matchDto.copy()

        Assertions.assertEquals(expectedResult, result)
    }

}

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}