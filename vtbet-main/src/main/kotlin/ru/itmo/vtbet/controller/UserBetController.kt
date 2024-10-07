package ru.itmo.vtbet.controller;

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.MakeBetRequest
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.service.UserBetService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class UserBetController(
    private val userBetService: UserBetService,
) {
    @PostMapping("/bet/{typeOfBetMatchId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun makeBet(
        @PathVariable("typeOfBetMatchId") id: Long,
        @RequestBody @Valid makeBetRequest: MakeBetRequest,
    ): ResponseEntity<BetResponse> {
        return ResponseEntity.ok(userBetService.makeBet(id, makeBetRequest).toResponse())
    }
}
