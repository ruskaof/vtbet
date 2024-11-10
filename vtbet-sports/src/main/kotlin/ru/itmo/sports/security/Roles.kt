package ru.itmo.sports.ru.itmo.sports.security

import org.springframework.security.core.GrantedAuthority

data class Roles(
    val id: Long,
    val name: String,
): GrantedAuthority {
    override fun getAuthority(): String {
        return name
    }
}
