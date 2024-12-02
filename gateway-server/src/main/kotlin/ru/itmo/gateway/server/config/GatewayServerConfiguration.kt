package ru.itmo.gateway.server.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayServerConfiguration {
    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("bets-handler-service") { r ->
                r.path("/bets-handler-service/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://personal-account:8083")
            }
            .route("sport-service") { r ->
                r.path("/sport-service/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://auth-service:8081")
            }
            .route("user-accounter-service") { r ->
                r.path("/user-accounter-service/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://game-handler:8082")
            }
            .build()
    }
}