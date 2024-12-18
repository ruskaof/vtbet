package ru.itmo.bets.handler.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.bets.handler.request.MakeBetRequestDto
import ru.itmo.bets.handler.service.ComplexUsersService
import ru.itmo.bets.handler.service.toResponse
import ru.itmo.common.response.BetResponse
import ru.itmo.common.utils.MAX_PAGE_SIZE

@Validated
@RestController
@RequestMapping("/bets/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User_bets_controller", description = "API for user_bets")
class UserBetController(
    private val complexUsersService: ComplexUsersService
) {
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun makeBet(
        @PathVariable("id") userId: Long,
        @RequestBody @Valid makeBetRequestDto: MakeBetRequestDto,
    ): BetResponse =
        complexUsersService.makeBet(userId, makeBetRequestDto).toResponse()

    @GetMapping("/{id}")
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
