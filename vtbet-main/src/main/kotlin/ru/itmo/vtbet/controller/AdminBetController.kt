package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.model.request.CreateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.request.UpdateTypeOfBetMatchRequest
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse
import ru.itmo.vtbet.service.AdminBetService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class AdminBetController(
    private val adminBetService: AdminBetService,
) {

    @PostMapping("admin/bet")
    fun createBetGroup(
        @Valid
        @RequestBody createBetGroupRequest: CreateBetGroupRequest,
    ) = adminBetService.createBetGroup(createBetGroupRequest).toResponse()

    @PutMapping("admin/bet/{id}")
    fun modifyBetGroup(
        @PathVariable id: Long,
        @Valid
        @RequestBody updateTypeOfBetMatchRequest: UpdateTypeOfBetMatchRequest,
    ) = adminBetService.updateBetMatch(id, updateTypeOfBetMatchRequest)

    @PostMapping("admin/matches/{matchId}/bet")
    fun createTypeOfBetMatch(
        @PathVariable matchId: Long,
        @RequestBody createTypeOfBetMatchRequest: CreateTypeOfBetMatchRequest,
    ): FullTypeOfBetMatchResponse =
        adminBetService.createTypeOfBetMatch(createTypeOfBetMatchRequest, matchId).toResponse()
}
