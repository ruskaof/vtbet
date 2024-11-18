package ru.itmo.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.auth.model.dto.UserDto
import ru.itmo.auth.model.entity.UsersEntity
import ru.itmo.auth.repository.RolesRepository
import ru.itmo.auth.repository.UsersRepository
import ru.itmo.common.exception.AuthException
import ru.itmo.common.exception.DuplicateException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.response.JwtResponseDto
import ru.itmo.common.utils.Role
import kotlin.jvm.optionals.getOrNull

@Service
class AuthService(
    private val usersRepository: UsersRepository,
    private val rolesRepository: RolesRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    @Transactional
    fun register(request: UserPasswordRequestDto): JwtResponseDto {
        if (usersRepository.findByUsername(request.username).isPresent) {
            throw DuplicateException("User with username ${request.username} already exists")
        }
        val user = usersRepository.saveAndFlush(
            UsersEntity(
                userId = null,
                username = request.username,
                password = passwordEncoder.encode(request.password),
                roles = setOf(rolesRepository.findByName(Role.USER)),
            )
        )
        val token = jwtService.generateAccessToken(
            UserDto(
                userId = user.userId!!,
                username = user.username,
                roles = user.roles.map { it.name }.toSet()
            )
        )
        return JwtResponseDto(user.userId, token)
    }

    fun login(request: UserPasswordRequestDto): JwtResponseDto {
        val user = usersRepository.findByUsername(request.username)
            .getOrNull()

        if (user == null || !passwordEncoder.matches(request.password, user.password)) {
            throw AuthException("invalid username or password")
        }

        val token = jwtService.generateAccessToken(
            UserDto(
                userId = user.userId!!,
                username = user.username,
                roles = user.roles.map { it.name }.toSet()
            )
        )
        return JwtResponseDto(user.userId, token)
    }
}