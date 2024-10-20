package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.UsersAccountsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.UsersAccountsRepository
import ru.itmo.vtbet.service.MatchesService
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional

class MatchesServiceTest {
    private val matchesRepository = Mockito.mock(MatchesRepository::class.java)

    private val matchesService =
        MatchesService(matchesRepository)

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

        val result = matchesService.getMatches(PageRequest.of(pageNumber, pageSize))
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

        val result = matchesService.getMatches(sportId, PageRequest.of(pageNumber, pageSize))
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

        val result = matchesService.save(matchDto)


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


        val result = matchesService.update(matchDto)

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

        val result = matchesService.getMatch(matchId)


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

        matchesService.delete(matchId)

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


        matchesService.endMatch(matchId)


        verify(matchesRepository).save(matchesEntity.copy(ended = true))
    }

    @Test
    fun `end match with exception`() {
        val matchId = 1L

        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> { matchesService.endMatch(matchId) }

    }

}
