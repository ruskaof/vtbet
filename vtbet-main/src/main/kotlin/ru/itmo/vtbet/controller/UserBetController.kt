package ru.itmo.vtbet.controller;

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.MakeBetRequestDto
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.service.ComplexUsersService
import ru.itmo.vtbet.service.MAX_PAGE_SIZE
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class UserBetController(
    private val complexUsersService: ComplexUsersService
) {
    @PostMapping("/users/{id}/bets")
    @ResponseStatus(HttpStatus.CREATED)
    fun makeBet(
        @PathVariable("id") userId: Long,
        @RequestBody @Valid makeBetRequestDto: MakeBetRequestDto,
    ): BetResponse =
        complexUsersService.makeBet(userId, makeBetRequestDto).toResponse()

    @GetMapping("/users/{id}/bets")
    fun getBets(
        @PathVariable("id") userId: Long,
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<BetResponse>> {
        val result = complexUsersService.getUserBets(userId, pageNumber, pageSize)
        return ResponseEntity(
            result.items.map { it.toResponse() },
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }
}
