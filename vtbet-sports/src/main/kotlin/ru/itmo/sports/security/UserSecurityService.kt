package ru.itmo.sports.ru.itmo.sports.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.itmo.common.dto.RoleDto
import ru.itmo.common.dto.UserDto
import ru.itmo.common.response.UserResponse
import ru.itmo.sports.ru.itmo.sports.clients.UsersClient


@Service
class UserSecurityService(
    val usersClient: UsersClient
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = usersClient
            .getUser(username)
            .toDto()

        return user.buildUserDetails()
    }

    fun UserDto.buildUserDetails(): UserDetailsImpl {
        return UserDetailsImpl(
            this.username,
            this.password,
            listOf(
                Roles(
                    id = this.role.id,
                    name = this.role.name,
                ),
            ),
        )
    }

    private fun UserResponse.toDto() =
        UserDto(
            userId = this.id,
            username = this.username,
            email = this.email,
            phoneNumber = this.phoneNumber,
            accountVerified = this.accountVerified,
            registrationDate = this.registrationDate,
            role = RoleDto(
                id = this.role.id,
                name = this.role.name,
            ),
            password = this.password,
        )
}
