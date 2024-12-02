package ru.itmo.auth.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.itmo.auth.service.AuthService
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.response.JwtResponseDto

@RestController
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/auth/register")
    fun register(@RequestBody request: UserPasswordRequestDto): JwtResponseDto =
        authService.register(request)

    @PostMapping("/auth/login")
    fun login(@RequestBody request: UserPasswordRequestDto): JwtResponseDto =
        authService.login(request)
}
