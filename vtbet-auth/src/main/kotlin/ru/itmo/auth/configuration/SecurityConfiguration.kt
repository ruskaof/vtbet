package ru.itmo.auth.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration(
    private val jwtDecoder: JwtDecoder
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .logout { it.disable() }
            .oauth2ResourceServer {
                it.jwt { jwt ->
                    jwt.decoder(jwtDecoder)
                }
            }
            .formLogin { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
            .build()
}