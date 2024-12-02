package ru.itmo.user.accounter.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.CreateUserRequestDto
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.common.response.UserResponse
import ru.itmo.common.utils.MAX_PAGE_SIZE
import ru.itmo.user.accounter.model.dto.UserAccountDto
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.toResponse

@RestController
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User_controller", description = "API for user")
class UsersController(
    private val usersAccountsService: UsersAccountsService
) {
    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable("id") userId: Long,
    ): Mono<UserResponse> = usersAccountsService.getUserAccount(userId).map { it.toResponse() }

    @GetMapping("/users/not-verified")
    fun getNotVerifiedUsers(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): Mono<PagingDto<UserAccountDto>> = usersAccountsService.getNotVerifiedUsers(pageSize, pageNumber)

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

    @PostMapping("users/{id}/balance")
    fun addMoney(
        @PathVariable("id") userId: Long,
        @RequestBody
        @Valid
        request: BalanceActionRequestDto,
    ): Mono<UserResponse> = usersAccountsService.handleBalanceAction(userId, request.amount, request.action).map { it.toResponse() }
}
