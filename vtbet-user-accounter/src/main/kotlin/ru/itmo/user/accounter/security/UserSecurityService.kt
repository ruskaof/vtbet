package ru.itmo.user.accounter.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.itmo.user.accounter.model.entity.UsersEntity
import ru.itmo.user.accounter.repository.UsersRepository
import kotlin.jvm.optionals.getOrNull


@Service
class UserSecurityService(
    val usersRepository: UsersRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = usersRepository
            .findByUsername(username)
            .blockOptional()
            .getOrNull()
            ?: throw UsernameNotFoundException("User Not Found with username: $username")

        return user.buildUserDetails()
    }

    fun getUser(): UsersEntity {
        val username =
            (SecurityContextHolder
                .getContext()
                .authentication as UsernamePasswordAuthenticationToken).name
        return usersRepository
            .findByUsername(username)
            .blockOptional()
            .getOrNull()
            ?: throw UsernameNotFoundException("User Not Found with username: $username")
    }

    fun UsersEntity.buildUserDetails(): UserDetailsImpl {
        return UserDetailsImpl(
            this.username,
            this.password,
            listOf(this.role),
        )
    }
}
