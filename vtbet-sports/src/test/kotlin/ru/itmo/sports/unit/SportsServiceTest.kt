package ru.itmo.sports.unit


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.CreateSportRequestDto
import ru.itmo.common.request.UpdateSportRequestDto
import ru.itmo.sports.ru.itmo.sports.model.dto.SportDto
import ru.itmo.sports.ru.itmo.sports.model.entity.SportsEntity
import ru.itmo.sports.ru.itmo.sports.repository.SportsRepository
import ru.itmo.sports.ru.itmo.sports.service.SportsService
import java.util.*
import kotlin.test.assertEquals

class SportsServiceTest {
    private val sportsRepository = mock(SportsRepository::class.java)

    private val sportService = SportsService(sportsRepository)

    @Test
    fun `get sport by id`() {
        val sportId = 1L
        val sportName = "football"
        val sportEntity = SportsEntity(sportId, sportName)

        `when`(sportsRepository.findById(sportId)).thenReturn(Optional.of(sportEntity))

        val result = sportService.getSport(sportId)

        val expectedResult = SportDto(
            sportId = sportId,
            name = sportName,
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get sports`() {
        val sportId = 1L
        val sportName = "football"
        val sport2Id = 1L
        val sport2Name = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(SportsEntity(sportId, sportName), SportsEntity(sport2Id, sport2Name)))

        `when`(sportsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("sportId")))).thenReturn(pageSports)

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
    fun `delete sport`() {
        val sportId = 1L

        `when`(sportsRepository.existsById(sportId)).thenReturn(true)

        sportService.deleteSport(sportId)

        verify(sportsRepository).deleteById(sportId)
    }

    @Test
    fun `delete sport with exception`() {
        val sportId = 1L

        `when`(sportsRepository.existsById(sportId)).thenReturn(false)

        val exception = assertThrows<ResourceNotFoundException> {sportService.deleteSport(sportId)}

        assertEquals(exception.message, "Sport with id 1 not found")
    }
    @Test
    fun `update sport`() {
        val sportId = 1L
        val sportName = "football"
        val request = UpdateSportRequestDto(
            name = "football",
        )

        `when`(sportsRepository.findById(sportId)).thenReturn(Optional.of(SportsEntity(sportId, sportName)))
        `when`(sportsRepository.save(any())).thenReturn(SportsEntity(sportId, sportName))

        sportService.updateSport(sportId, request)

        verify(sportsRepository).save(
            SportsEntity(sportId, "football")
        )
    }

    @Test
    fun `update sport with exception`() {
        val sportId = 1L
        val request = UpdateSportRequestDto(
            name = "football",
        )

        `when`(sportsRepository.findById(sportId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> { sportService.updateSport(sportId, request)}


        assertEquals(exception.message, "Sport with id 1 not found")
    }
}
