package ru.itmo.vtbet.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono
import ru.itmo.common.utils.Claim
import ru.itmo.common.utils.Role

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration(
    private val securityProperties: SecurityProperties,
) {

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder.withPublicKey(securityProperties.publicKey)
            .build()

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .logout { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.jwtDecoder(jwtDecoder()) } }
            .authorizeExchange {
                it
                    // sport, matches
                    .pathMatchers(HttpMethod.GET, "/service-sports/**").hasAuthority("SCOPE_${Role.USER}")
                    .pathMatchers("/service-sports/**").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    // auth
                    .pathMatchers("/service-auth/auth/register").hasAuthority("SCOPE_${Role.USER_VERIFIER}")
                    .pathMatchers("/service-auth/auth/login").permitAll()
                    // user account
                    .pathMatchers(HttpMethod.GET, "/service-user-accounter/users/{id}").access { authentication, context ->
                        matchesUserIdInPath(authentication, context)
                    }
                    .pathMatchers(HttpMethod.PATCH, "/service-user-accounter/users/{id}").access { authentication, context ->
                        matchesUserIdInPath(authentication, context)
                    }
                    .pathMatchers(HttpMethod.DELETE, "/service-user-accounter/users/{id}").access { authentication, context ->
                        matchesUserIdInPath(authentication, context)
                    }
                    .pathMatchers(HttpMethod.POST, "/service-user-accounter/users/{id}").hasAuthority("SCOPE_${Role.USER_VERIFIER}")
                    .pathMatchers("/service-user-accounter/users/{id}/balance").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    // bets
                    .pathMatchers("/service-bets-handler/bets/groups").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.GET, "/service-bets-handler/bets").hasAuthority("SCOPE_${Role.USER}")
                    .pathMatchers(HttpMethod.GET, "/service-bets-handler/bets/{id}").hasAuthority("SCOPE_${Role.USER}")
                    .pathMatchers(HttpMethod.GET, "/service-bets-handler/bets/matches/{id}").hasAuthority("SCOPE_${Role.USER}")
                    .pathMatchers(HttpMethod.POST, "/service-bets-handler/bets/{id}").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.POST, "/service-bets-handler/bets/matches/{id}").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.POST, "/service-bets-handler/bets/matches/{id}/results").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.DELETE, "/service-bets-handler/bets/{id}").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.PUT, "/service-bets-handler/bets/{id}/closed").hasAuthority("SCOPE_${Role.BET_ADMIN}")
                    .pathMatchers(HttpMethod.POST, "/service-bets-handler/bets/users/{id}").access { authentication, context ->
                        matchesUserIdInPath(authentication, context)
                    }
                    .anyExchange().denyAll()
            }
            .build()

    private fun matchesUserIdInPath(authentication: Mono<Authentication>, context: AuthorizationContext) =
        authentication.map { a -> a.principal as? Jwt }
            .map { jwt -> jwt?.getClaim<Long>(Claim.USER_ID).toString() == context.variables["id"] }
            .map(::AuthorizationDecision)
}