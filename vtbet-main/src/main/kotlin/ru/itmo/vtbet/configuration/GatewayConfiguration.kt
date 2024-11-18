package ru.itmo.vtbet.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("vtbet.gateway")
class GatewayUrls(
    val serviceAuth: String,
    val serviceBetsHandler: String,
    val serviceSports: String,
    val serviceUserAccounter: String
)

@Configuration
@EnableConfigurationProperties(GatewayUrls::class)
class GatewayConfiguration(
    private val gatewayUrls: GatewayUrls,
) {

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth") { r ->
                r.path("/service-auth/**")
                    .filters { it.stripPrefix(1) }
                    .uri(gatewayUrls.serviceAuth)
            }
            .route("bets") { r ->
                r.path("/service-bets-handler/**")
                    .filters { it.stripPrefix(1) }
                    .uri(gatewayUrls.serviceBetsHandler)
            }
            .route("sports") { r ->
                r.path("/service-sports/**")
                    .filters { it.stripPrefix(1) }
                    .uri(gatewayUrls.serviceSports)
            }
            .route("user-accounter") { r ->
                r.path("/service-user-accounter/**")
                    .filters { it.stripPrefix(1) }
                    .uri(gatewayUrls.serviceUserAccounter)
            }
            .build()
    }
}