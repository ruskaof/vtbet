package ru.itmo.user.accounter.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority


class JwtAuthentication : Authentication {
    private var isAuthenticated: Boolean = false
    private val name: String? = null

    override fun getName(): String? {
        return name
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return null
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return name
    }

    override fun isAuthenticated(): Boolean {
        return isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }
}