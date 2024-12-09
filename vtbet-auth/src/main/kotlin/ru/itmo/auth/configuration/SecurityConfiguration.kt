package ru.itmo.auth.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableMethodSecurity
class SecurityConfiguration(
    private val jwtDecoder: JwtDecoder
) {

    @Configuration
    class CorsConfiguration {
        @Bean
        fun corsConfigurer(): WebMvcConfigurer {
            return object : WebMvcConfigurer {
                override fun addCorsMappings(registry: CorsRegistry) {
                    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*")
                }
            }
        }
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .logout { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.decoder(jwtDecoder) } }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
}