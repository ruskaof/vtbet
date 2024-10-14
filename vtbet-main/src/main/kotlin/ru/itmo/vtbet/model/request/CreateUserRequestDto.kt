package ru.itmo.vtbet.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateUserRequestDto(
    @field:Size(max = 255, min = 1, message = "String length must be between 1 and 255")
    val username: String,
    @field:Size(max = 255, min = 1, message = "String length must be between 1 and 255")
    @field:Email(message = "Invalid email address")
    val email: String? = null,
    @field:Size(max = 255, min = 1, message = "String length must be between 1 and 255")
    @field:Pattern(regexp = "[0-9]{10}", message = "Invalid phone number")
    val phoneNumber: String? = null,
)
