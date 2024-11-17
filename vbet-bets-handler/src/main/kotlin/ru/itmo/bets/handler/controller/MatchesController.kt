package ru.itmo.bets.handler.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.bets.handler.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.service.AvailableBetsService
import ru.itmo.bets.handler.service.ComplexBetsService
import ru.itmo.bets.handler.service.toResponse
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.common.response.AvailableBetsResponse
import ru.itmo.common.response.FullTypeOfBetMatchResponse
import ru.itmo.common.utils.MAX_PAGE_SIZE

@Validated
@RestController
@RequestMapping("/bets/matches")
class MatchesController(
    private val complexBetsService: ComplexBetsService,
    private val availableBetsService: AvailableBetsService,
) {
    @GetMapping("/{id}")
    fun getMatchBets(
        @PathVariable("id") matchId: Long,
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<AvailableBetsResponse>> {
        val result = availableBetsService.getAllByMatchId(matchId, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(AvailableBetDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PostMapping("/{id}")
    fun createTypeOfBetMatch(
        @PathVariable("id") matchId: Long,
        @RequestBody createAvailableBetRequestDto: CreateAvailableBetRequestDto,
    ): FullTypeOfBetMatchResponse =
        complexBetsService.createAvailableBet(matchId, createAvailableBetRequestDto).toResponse()

    @PostMapping("/{id}/results")
    fun countResultsForMatch(
        @PathVariable(name = "id") matchId: Long,
        @RequestBody successfulAvailableBetsIds: Set<Long>,
    ) = complexBetsService.countResultsForMatch(matchId, successfulAvailableBetsIds)
}
