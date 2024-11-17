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
import ru.itmo.common.response.UserResponse
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.toResponse

@RestController
@Validated
class UsersController(
    private val usersAccountsService: UsersAccountsService
) {
    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable("id") userId: Long,
    ): Mono<UserResponse> = usersAccountsService.getUserAccount(userId).map { it.toResponse() }

    @PostMapping("/users/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @RequestBody
        @Valid
        request: CreateUserRequestDto,
        @PathVariable("id") userId: Long,
    ) = usersAccountsService.createUserAccount(request, userId)

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable("id") userId: Long,
    ): Mono<Void> = usersAccountsService.delete(userId)

    @PatchMapping("users/{id}")
    fun updateUser(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: UpdateUserRequestDto,
    ): Mono<UserResponse> = usersAccountsService.updateUserAccount(request, userId).map { it.toResponse() }
        .switchIfEmpty(Mono.error(ResourceNotFoundException("User with ID $userId not found")))

    @PostMapping("users/{id}/balance")
    fun addMoney(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: BalanceActionRequestDto,
    ): Mono<UserResponse> = usersAccountsService.handleBalanceAction(userId, request.amount, request.action).map { it.toResponse() }
        .switchIfEmpty(Mono.error(ResourceNotFoundException("User with ID $userId not found")))
}
