package ru.itmo.user.accounter.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.user.accounter.service.ComplexUsersService
import ru.itmo.user.accounter.service.toResponse

@RestController
@Validated
class UsersController(
    private val complexUsersService: ComplexUsersService,
) {
    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable("id") userId: Long,
    ) = complexUsersService.getUser(userId).map { it.toResponse() }


    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @RequestBody
        @Valid
        request: CreateUserRequestDto,
    ) =
        complexUsersService.createUser(request)

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable("id") userId: Long,
    ) = complexUsersService.deleteUser(userId)
        .switchIfEmpty(Mono.error(ResourceNotFoundException("User with ID $userId not found")))

    @PutMapping("users/{id}")
    fun updateUser(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: UpdateUserRequestDto,
    ) = complexUsersService.updateUser(userId, request).map { it.toResponse() }
        .switchIfEmpty(Mono.error(ResourceNotFoundException("User with ID $userId not found")))

    @PostMapping("users/{id}/balance")
    fun addMoney(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: BalanceActionRequestDto,
    ) = complexUsersService.handleBalanceAction(userId, request.amount, request.action).map { it.toResponse() }
        .switchIfEmpty(Mono.error(ResourceNotFoundException("User with ID $userId not found")))
}
