package ru.itmo.auth

import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import ru.itmo.auth.repository.RolesRepository
import ru.itmo.auth.repository.UsersRepository
import ru.itmo.auth.service.AuthService
import ru.itmo.auth.service.JwtService

class AuthServiceTest {

    private val usersRepository = Mockito.mock(UsersRepository::class.java)
    private val rolesRepository = Mockito.mock(RolesRepository::class.java)
    private val passwordEncoder = Mockito.mock(PasswordEncoder::class.java)
    private val jwtService = Mockito.mock(JwtService::class.java)

    private val authService: AuthService = AuthService(
        usersRepository = usersRepository,
        rolesRepository = rolesRepository,
        passwordEncoder = passwordEncoder,
        jwtService = jwtService
    )

}