package ru.itmo.bets.handler.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "bets-handler",
        description = "betting service",
        version = "1.0.0",
        contact = Contact(
            name = "ruskaof, svytoq, sasaovch"
        )
    )
)
@Configuration
class SwaggerConfiguration