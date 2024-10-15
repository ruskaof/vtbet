package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.CreateBetsGroupsRequestDto
import ru.itmo.vtbet.model.request.UpdateAvailableBetRequestDto
import ru.itmo.vtbet.model.response.AvailableBetsResponse
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse
import ru.itmo.vtbet.service.AdminBetService
import ru.itmo.vtbet.service.BetsService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class AdminBetController(
    private val adminBetService: AdminBetService,
    private val betsService: BetsService,
) {
    @PostMapping("admin/bets/groups")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBetGroup(
        @Valid
        @RequestBody createBetsGroupsRequestDto: CreateBetsGroupsRequestDto,
    ) = betsService.createBetGroup(createBetsGroupsRequestDto).map { it.toResponse() }

    @PutMapping("admin/bets/{id}")
    fun modifyBetGroup(
        @PathVariable("id") betId: Long,
        @Valid
        @RequestBody updateAvailableBetRequestDto: UpdateAvailableBetRequestDto,
    ): AvailableBetsResponse = adminBetService.updateAvailableBet(betId, updateAvailableBetRequestDto).toResponse()

    @PostMapping("admin/matches/{id}/bets")
    fun createTypeOfBetMatch(
        @PathVariable("id") matchId: Long,
        @RequestBody createAvailableBetRequestDto: CreateAvailableBetRequestDto,
    ): FullTypeOfBetMatchResponse =
        adminBetService.createAvailableBet(matchId, createAvailableBetRequestDto).toResponse()
}
