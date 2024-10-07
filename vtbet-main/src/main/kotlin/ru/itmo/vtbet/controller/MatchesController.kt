package ru.itmo.vtbet.controller

import jakarta.validation.constraints.Max
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        @RequestParam pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam pageSize: Int,
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
        @RequestParam pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @RequestParam pageSize: Int,
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
    fun deleteMatch(
        @PathVariable("matchId") matchId: Long,
    ) = matchesService.delete(matchId)

    @PatchMapping("match/{matchId}/end")
    fun endMatch(
        @PathVariable("matchId") matchId: Long,
        @RequestBody successfulBets: Set<Long>,
    ) = matchesService.endMatch(matchId, successfulBets)
}
