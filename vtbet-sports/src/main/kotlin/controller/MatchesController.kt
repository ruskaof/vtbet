package ru.itmo.sports.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.common.utils.MAX_PAGE_SIZE
import ru.itmo.sports.model.dto.MatchDto
import ru.itmo.common.request.CreateMatchRequestDto
import ru.itmo.common.request.UpdateMatchRequestDto
import ru.itmo.common.response.MatchResponse
import ru.itmo.sports.service.ComplexMatchesService
import ru.itmo.sports.service.toResponse


@RestController
@Validated
class MatchesController(
    private val complexMatchesService: ComplexMatchesService,
) {
    @GetMapping("/matches")
    fun getMatches(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<MatchResponse>> {
        val result = complexMatchesService.getMatches(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(MatchDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

//    @GetMapping("/matches/{id}/bets")
//    fun getMatchBets(
//        @PathVariable("id") matchId: Long,
//        @PositiveOrZero
//        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
//        @Max(MAX_PAGE_SIZE)
//        @Positive
//        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
//    ): ResponseEntity<List<AvailableBetsResponse>> {
//        val result = availableBetsService.getAllByMatchId(matchId, pageNumber, pageSize)
//        return ResponseEntity(
//            result.items.map(AvailableBetDto::toResponse),
//            preparePagingHeaders(result.total, result.page, result.pageSize),
//            HttpStatus.OK
//        )
//    }

    @PutMapping("matches/{id}")
    fun updateMatch(
        @PathVariable id: Long,
        @RequestBody updateMatchRequestDto: UpdateMatchRequestDto,
    ): MatchResponse =
        complexMatchesService.updateMatch(updateMatchRequestDto, id).toResponse()

    @DeleteMapping("matches/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMatch(
        @PathVariable id: Long,
    ): Unit = complexMatchesService.delete(id)

    @PostMapping("/matches")
    @ResponseStatus(HttpStatus.CREATED)
    fun createMatch(
        @RequestBody @Valid createMatchRequestDto: CreateMatchRequestDto,
    ): MatchResponse =
        complexMatchesService.createMatch(createMatchRequestDto).toResponse()
}
