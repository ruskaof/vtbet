package ru.itmo.vtbet.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SimpleAvailableBetsDto
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SimpleAvailableBetsResponse
import ru.itmo.vtbet.service.ComplexMatchesService
import ru.itmo.vtbet.service.MAX_PAGE_SIZE
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class MatchesController(
    private val complexMatchesService: ComplexMatchesService,
) {
    @GetMapping("/matches")
    fun getMatches(
        @PositiveOrZero
        @RequestParam(defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @PositiveOrZero
        @RequestParam(defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<MatchResponse>> {
        val result = complexMatchesService.getMatches(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(MatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @GetMapping("/match/{id}/bets")
    fun getMatchBets(
        @PathVariable("id") matchId: Long,
        @PositiveOrZero
        @RequestParam(defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam(defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<SimpleAvailableBetsResponse>> {
        val result = complexMatchesService.getBetsByMatchId(matchId, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SimpleAvailableBetsDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PatchMapping("match/{id}")
    fun updateMatch(
        @PathVariable id: Long,
        @RequestBody updateMatchRequestDto: UpdateMatchRequestDto,
    ): MatchResponse =
        complexMatchesService.updateMatch(updateMatchRequestDto, id).toResponse()

    @DeleteMapping("match/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMatch(
        @PathVariable id: Long,
    ): Unit = complexMatchesService.delete(id)

    @PostMapping("match/{id}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun endMatch(
        @PathVariable id: Long,
        @RequestBody successfulBets: Set<Long>,
    ): Unit = complexMatchesService.endMatch(id, successfulBets)

    @GetMapping("matches/{id}/types-of-bets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTypeOfBetMatch(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam(defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<SimpleAvailableBetsResponse>> {
        val result = sportsService.getTypeOfBetMatch(id, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SimpleAvailableBetsDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }
}
