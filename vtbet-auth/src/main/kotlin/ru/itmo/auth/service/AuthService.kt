package ru.itmo.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.auth.model.dto.UserDto
import ru.itmo.auth.model.entity.UsersEntity
import ru.itmo.auth.repository.RolesRepository
import ru.itmo.auth.repository.UsersRepository
import ru.itmo.common.request.UserPasswordRequestDto
import ru.itmo.common.response.JwtResponseDto
import ru.itmo.common.utils.Role

@Service
class AuthService(
    private val usersRepository: UsersRepository,
    private val rolesRepository: RolesRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    @Transactional
    fun register(request: UserPasswordRequestDto): JwtResponseDto {
        // todo check duplicate username
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
        return JwtResponseDto(token)
    }

    fun login(request: UserPasswordRequestDto): JwtResponseDto {
        val user = usersRepository.findByUsername(request.username)
        if (!passwordEncoder.matches(request.password, user.password)) {
            error("invalid username or password") // fixme
        }

        val token = jwtService.generateAccessToken(
            UserDto(
                userId = user.userId!!,
                username = user.username,
                roles = user.roles.map { it.name }.toSet()
            )
        )
        return JwtResponseDto(token)
    }
}