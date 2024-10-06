package ru.itmo.vtbet.controller

import jakarta.validation.constraints.Max
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.toResponse
import ru.itmo.vtbet.model.request.CreateMatchRequest
import ru.itmo.vtbet.model.request.CreateSportRequest
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.service.MAX_PAGE_SIZE
import ru.itmo.vtbet.service.SportService

@RestController
@Validated
class SportController(
    private val sportService: SportService,
) {

    @GetMapping("/sport")
    fun getSports(
        @RequestParam pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam pageSize: Int,
    ): ResponseEntity<List<SportResponse>> {
        val result = sportService.getSports(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SportDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PostMapping("/sport")
    fun createSport(
        @RequestBody createSportRequest: CreateSportRequest,
    ): SportResponse =
        sportService.createSport(createSportRequest).toResponse()

    @GetMapping("/sport/{id}/matches")
    fun getMatches(
        @PathVariable id: Long,
        @RequestParam pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam pageSize: Int,
    ): ResponseEntity<List<MatchResponse>> {
        val result = sportService.getMatches(id, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(MatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PostMapping("/sport/{id}/matches")
    fun createMatch(
        @PathVariable id: Long,
        @RequestBody createMatchRequest: CreateMatchRequest,
    ): MatchResponse =
        sportService.createMatch(createMatchRequest, sportId = id).toResponse()
}