package ru.itmo.auth.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
@EnableConfigurationProperties(SecurityProperties::class)
class ApplicationConfiguration(
    private val securityProperties: SecurityProperties
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(
        ImmutableJWKSet(
            JWKSet(
                RSAKey.Builder(securityProperties.publicKey).privateKey(securityProperties.privateKey).build()
            )
        )
    )

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(securityProperties.publicKey).build()
}