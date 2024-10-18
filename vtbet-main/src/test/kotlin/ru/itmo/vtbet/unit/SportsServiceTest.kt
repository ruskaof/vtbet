package ru.itmo.vtbet.unit


import org.junit.Test
import org.junit.jupiter.api.Assertions
import ru.itmo.vtbet.repository.SportsRepository
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import ru.itmo.vtbet.service.SportsService
import java.util.*

class SportsServiceTest {
    private val sportsRepository = mock(SportsRepository::class.java)

    private val sportService = SportsService(sportsRepository)

    @Test
    fun `get sports`() {
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
    fun `get sport by id`() {
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

}
