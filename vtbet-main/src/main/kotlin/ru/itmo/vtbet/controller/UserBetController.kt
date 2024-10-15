package ru.itmo.vtbet.controller;

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.MakeBetRequestDto
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.service.ComplexUsersService
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
}
