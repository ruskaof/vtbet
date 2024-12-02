package ru.itmo.sports.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "sports",
        description = "sports service",
        version = "1.0.0",
        contact = Contact(
            name = "ruskaof, svytoq, sasaovch"
        )
    )
)
@Configuration
class SwaggerConfiguration