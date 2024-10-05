package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateUserRequest(
    @field:NotBlank(message = "Invalid username: must be not empty")
    val username: String,
    @field:Email(message = "Invalid email address")
    val email: String? = null,
    @field:Pattern(regexp = "[0-9]{10}", message = "Invalid phone number")
    val phoneNumber: String? = null,
)
