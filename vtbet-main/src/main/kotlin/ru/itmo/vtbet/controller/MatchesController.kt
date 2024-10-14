package ru.itmo.vtbet.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.request.UpdateMatchRequest
import ru.itmo.vtbet.model.response.MatchResponse
import ru.itmo.vtbet.model.response.SimpleTypeOfBetMatchResponse
import ru.itmo.vtbet.service.MAX_PAGE_SIZE
import ru.itmo.vtbet.service.MatchesService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class MatchesController(
    private val matchesService: MatchesService,
) {
    @GetMapping("/matches")
    fun getMatches(
        @PositiveOrZero
        @RequestParam(defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @PositiveOrZero
        @RequestParam(defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<MatchResponse>> {
        val result = matchesService.getMatches(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(MatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @GetMapping("/match/{id}/bets")
    fun getMatchBets(
        @PathVariable("id") id: Long,
        @PositiveOrZero
        @RequestParam(defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam(defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<SimpleTypeOfBetMatchResponse>> {
        val result = matchesService.getBetsByMatchId(id, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SimpleTypeOfBetMatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PatchMapping("match/{matchId}")
    fun updateMatch(
        @PathVariable("matchId") matchId: Long,
        @RequestBody updateMatchRequest: UpdateMatchRequest,
    ): MatchResponse =
        matchesService.updateMatch(updateMatchRequest, matchId).toResponse()

    @DeleteMapping("match/{matchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMatch(
        @PathVariable("matchId") matchId: Long,
    ): Unit = matchesService.delete(matchId)

    @PostMapping("match/{matchId}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun endMatch(
        @PathVariable("matchId") matchId: Long,
        @RequestBody successfulBets: Set<Long>,
    ): Unit = matchesService.endMatch(matchId, successfulBets)
}
