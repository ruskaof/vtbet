package ru.itmo.vtbet.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "vtbet",
        description = "betting service",
        version = "1.0.0",
        contact = Contact(
            name = "ruskaof, svytoq, sasaovch"
        )
    )
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@Configuration
class OpenApiConfig
