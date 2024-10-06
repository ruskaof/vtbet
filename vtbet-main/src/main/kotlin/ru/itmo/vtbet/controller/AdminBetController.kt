package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.model.dto.toResponse
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.service.AdminBetService

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
}