package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.model.request.CreateBetGroupRequestDto
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.UpdateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse
import ru.itmo.vtbet.model.response.SimpleAvailableBetsResponse
import ru.itmo.vtbet.service.AdminBetService
import ru.itmo.vtbet.service.BetsService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class AdminBetController(
    private val adminBetService: AdminBetService,
    private val betsService: BetsService,
) {

    @PostMapping("admin/bet/group")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBetGroup(
        @Valid
        @RequestBody createBetGroupRequestDto: CreateBetGroupRequestDto,
    ) = betsService.createBetGroup(createBetGroupRequestDto).toResponse()

    @PutMapping("admin/bet/{id}")
    fun modifyBetGroup(
        @PathVariable id: Long,
        @Valid
        @RequestBody updateTypeOfBetMatchRequest: UpdateTypeOfBetMatchRequest,
    ): SimpleAvailableBetsResponse = adminBetService.updateAvailableBet(id, updateTypeOfBetMatchRequest)

    @PostMapping("admin/matches/{id}/bet")
    fun createTypeOfBetMatch(
        @PathVariable id: Long,
        @RequestBody createAvailableBetRequestDto: CreateAvailableBetRequestDto,
    ): FullTypeOfBetMatchResponse =
        adminBetService.createAvailableBet(createAvailableBetRequestDto, id).toResponse()
}
