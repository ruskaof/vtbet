package ru.itmo.vtbet.configuration

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfiguration {

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth") { r ->
                r.path("/service-auth/**")
                    .filters { it.stripPrefix(1) }
                    .uri("http://localhost:8440")
            }
            .route("bets") { r ->
                r.path("/service-bets-handler/**")
                    .filters { it.stripPrefix(1) }
                    .uri("http://localhost:8492")
            }
            .route("sports") { r ->
                r.path("/service-sports/**")
                    .filters { it.stripPrefix(1) }
                    .uri("http://localhost:8081")
            }
            .route("user-accounter") { r ->
                r.path("/service-user-accounter/**")
                    .filters { it.stripPrefix(1) }
                    .uri("http://localhost:8601")
            }
            .build()
    }
}