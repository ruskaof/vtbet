package ru.itmo.bets.handler.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.response.UserResponse

@FeignClient(name = "user-accounter-client", url = "http://localhost:8601/users")
interface UserAccountClient {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): UserResponse

    @PostMapping("users/{id}/balance")
    fun updateBalance(
        @PathVariable id: Long,
        @RequestBody balanceActionRequestDto: BalanceActionRequestDto
    ): UserResponse
}