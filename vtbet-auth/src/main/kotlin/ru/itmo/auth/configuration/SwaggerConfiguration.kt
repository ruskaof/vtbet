package ru.itmo.auth.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "auth",
        description = "auth service",
        version = "1.0.0",
        contact = Contact(
            name = "ruskaof, svytoq, sasaovch"
        )
    )
)
@Configuration
class OpenApiConfig
