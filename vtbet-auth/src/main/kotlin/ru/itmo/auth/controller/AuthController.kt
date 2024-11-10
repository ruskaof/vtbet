package ru.itmo.auth.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.itmo.auth.service.AuthService
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.response.JwtResponseDto

@Controller
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