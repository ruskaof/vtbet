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
            } // fixme add other routes
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<VtbetApplication>(*args)
}
