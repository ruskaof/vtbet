package ru.itmo.user.accounter.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.user.accounter.service.ComplexUsersService
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.user.accounter.service.toResponse
import ru.itmo.common.response.UserResponse

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
