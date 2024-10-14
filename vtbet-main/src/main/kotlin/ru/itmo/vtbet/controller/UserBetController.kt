package ru.itmo.vtbet.controller;

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.MakeBetRequestDto
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.service.UserBetService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class UserBetController(
    private val userBetService: UserBetService,
) {
    @PostMapping("/users/{userId}/bets")
    @ResponseStatus(HttpStatus.CREATED)
    fun makeBet(
        @PathVariable("userId") userId: Long,
        @RequestBody @Valid makeBetRequestDto: MakeBetRequestDto,
    ): ResponseEntity<BetResponse> {
        return ResponseEntity.ok(userBetService.makeBet(userId, makeBetRequestDto).toResponse())
    }
}
