package ru.itmo.sports.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import ru.itmo.sports.ru.itmo.sports.security.UserDetailsImpl
import java.security.Key
import java.util.*

@Component
class JwtUtils {
    //FIXME: add into property file
    @Value("\${inquisition.app.jwtSecret}")
    private val jwtSecret: String? = null

    @Value("\${inquisition.app.jwtExpirationMs}")
    private val jwtExpirationMs = 0

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetailsImpl

        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(Date())
            .setExpiration(Date(Date().getTime() + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact()
    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts
            .parser()
            .setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject()
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken)
            return true
        //FIXME: how to handle
        } catch (e: MalformedJwtException) {
//            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
//            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
//            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
//            logger.error("JWT claims string is empty: {}", e.message)
        }

        return false
    }
}