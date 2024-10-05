package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.model.dto.toResponse
import ru.itmo.vtbet.model.request.AddMoneyRequest
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.model.response.UserResponse
import ru.itmo.vtbet.service.UserService

@RestController
@Validated
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable id: Long,
    ): UserResponse? = userService.getUser(id).toResponse()

    @PostMapping("/users")
    fun createUser(
        @RequestBody
        @Valid
        request: CreateUserRequest,
    ): UserResponse? =
        userService.createUser(request).toResponse()

    @PostMapping("users/{id}/balance/add")
    fun addMoney(
        @PathVariable
        id: Long,
        @RequestBody
        @Valid
        request: AddMoneyRequest,
    ): Unit =
        userService.addMoneyToUser(id, request.amount)
}