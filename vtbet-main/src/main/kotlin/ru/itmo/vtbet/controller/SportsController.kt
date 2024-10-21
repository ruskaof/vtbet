package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import ru.itmo.vtbet.model.request.UpdateSportRequestDto
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.service.ComplexMatchesService
import ru.itmo.vtbet.service.MAX_PAGE_SIZE
import ru.itmo.vtbet.service.SportsService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class SportsController(
    private val sportsService: SportsService,
    private val complexMatchesService: ComplexMatchesService,
) {
    @GetMapping("/sports")
    fun getSports(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<SportResponse>> {
        val result = sportsService.getSports(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SportDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PostMapping("/sports")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSport(
        @RequestBody @Valid createSportRequestDto: CreateSportRequestDto,
    ): SportResponse =
        sportsService.createSport(createSportRequestDto).toResponse()

    @DeleteMapping("/sports/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSport(@PathVariable("id") sportId: Long): Unit =
        sportsService.deleteSport(sportId)

    @GetMapping("/sports/{id}/matches")
    fun getMatches(
        @PathVariable("id") sportId: Long,
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<MatchResponse>> {
        val result = complexMatchesService.getMatches(sportId, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(MatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PutMapping("/sports/{id}")
    fun updateSport(
        @PathVariable("id") sportId: Long,
        @RequestBody @Valid updateMatchRequestDto: UpdateSportRequestDto,
    ): SportResponse = sportsService.updateSport(sportId, updateMatchRequestDto).toResponse()
}
