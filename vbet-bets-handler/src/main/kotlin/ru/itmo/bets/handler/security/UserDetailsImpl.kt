package ru.itmo.bets.handler.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


data class UserDetailsImpl(
    val username: String,
    val password: String,
    val authorities: Collection<Roles>,
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.toMutableList()
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
