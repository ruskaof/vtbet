package ru.itmo.vtbet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class VtbetApplication {
    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("sports") { r ->
                r.path("/sports/**")
                    .uri("http://localhost:8081")
            }
            .route("bets") { r ->
                r.path("/bets/**")
                    .uri("http://localhost:8492")
            }
            .route("matches") { r ->
                r.path("/matches/**")
                    .uri("http://localhost:8081")
            }
            .route("users") { r ->
                r.path("/users/**")
                    .uri("http://localhost:8601")
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<VtbetApplication>(*args)
}
