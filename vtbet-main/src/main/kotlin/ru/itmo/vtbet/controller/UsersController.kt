package ru.itmo.vtbet.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.request.BalanceActionRequestDto
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.model.request.UpdateUserRequestDto
import ru.itmo.vtbet.model.response.UserResponse
import ru.itmo.vtbet.service.ComplexUsersService
import ru.itmo.vtbet.service.toResponse

@RestController
@Validated
class UsersController(
    private val complexUsersService: ComplexUsersService,
) {
    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable("id") userId: Long,
    ): UserResponse? = complexUsersService.getUser(userId)?.toResponse()
        ?: throw ResourceNotFoundException("User with ID $userId not found")

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @RequestBody
        @Valid
        request: CreateUserRequestDto,
    ): UserResponse? =
        complexUsersService.createUser(request).toResponse()

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable("id") userId: Long,
    ): Unit = complexUsersService.deleteUser(userId)

    @PutMapping("users/{id}")
    fun updateUser(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: UpdateUserRequestDto,
    ): UserResponse = complexUsersService.updateUser(userId, request).toResponse()

    @PostMapping("users/{id}/balance")
    fun addMoney(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: BalanceActionRequestDto,
    ): UserResponse = complexUsersService.handleBalanceAction(userId, request.amount, request.action).toResponse()
}
