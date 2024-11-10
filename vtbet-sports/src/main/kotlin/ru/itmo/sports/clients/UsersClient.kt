package ru.itmo.sports.ru.itmo.sports.clients

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.response.UserResponse

@FeignClient(name = "users-client", url = "http://localhost:8601/api/users")
interface UsersClient {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): UserResponse

    //FIXME: add to service
    @GetMapping("/users/{username}")
    fun getUser(@PathVariable username: String): UserResponse

    @PostMapping("/users/{id}/balance")
    fun updateBalance(@PathVariable id: Long, requestDto: BalanceActionRequestDto)
}
