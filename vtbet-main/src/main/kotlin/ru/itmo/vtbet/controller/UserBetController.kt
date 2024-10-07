package ru.itmo.vtbet.controller;

import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.model.request.MakeBetRequest
import ru.itmo.vtbet.model.response.BetResponse
import ru.itmo.vtbet.service.UserBetService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class UserBetController(
    private val userBetService: UserBetService,
) {
    @PostMapping("/bet/{id}")
    fun makeBet(
        @PathVariable("id") id: Long,
        @RequestParam makeBetRequest: MakeBetRequest,
    ): ResponseEntity<BetResponse> {
        return ResponseEntity.ok(userBetService.makeBet(id, makeBetRequest).toResponse())
    }
}
