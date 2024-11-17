package ru.itmo.auth.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
class SecurityConfiguration(
    private val jwtDecoder: JwtDecoder
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .logout { it.disable() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.decoder(jwtDecoder) } }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
}