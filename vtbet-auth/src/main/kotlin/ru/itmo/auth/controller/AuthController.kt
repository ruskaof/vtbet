package ru.itmo.auth.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.itmo.auth.service.AuthService
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.response.JwtResponseDto
import ru.itmo.common.utils.Role

@RestController
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/auth/register")
    fun register(@RequestBody request: UserPasswordRequestDto): JwtResponseDto =
        authService.register(request)

    @PreAuthorize("hasAuthority('SCOPE_${Role.USER_VERIFIER}')")
    @PostMapping("/auth/login")
    fun login(@RequestBody request: UserPasswordRequestDto): JwtResponseDto =
        authService.login(request)

    @PreAuthorize("hasAuthority('SCOPE_${Role.SERVICES_ADMIN}')")
    @PostMapping("/auth/service/{service}")
    fun loginService(@PathVariable service: String): JwtResponseDto =
        authService.loginService(service)
}
