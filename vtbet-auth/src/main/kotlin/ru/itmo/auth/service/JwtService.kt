package ru.itmo.auth.service

import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import ru.itmo.auth.model.dto.UserDto
import ru.itmo.common.utils.Claim
import java.time.Duration
import java.time.Instant

@Service
class JwtService(private val jwtEncoder: JwtEncoder) {

    fun generateAccessToken(userDto: UserDto): String {
        val now = Instant.now()
        return jwtEncoder.encode(
            JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now + Duration.ofHours(1))
                .claim(Claim.ROLES, userDto.roles)
                .claim(Claim.USERNAME, userDto.username)
                .claim(Claim.USER_ID, userDto.userId)
                .build()
                .let(JwtEncoderParameters::from)
        ).tokenValue
    }

}