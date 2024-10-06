package ru.itmo.vtbet.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.vtbet.model.dto.toResponse
import ru.itmo.vtbet.model.response.UserResponse
import ru.itmo.vtbet.service.BetService

@RestController
@Validated
class BetController(
        private val betService: BetService,
) {
    @GetMapping("/sport")
    fun getUser(
    ): UserResponse? = userService.getUser(id).toResponse()


}
