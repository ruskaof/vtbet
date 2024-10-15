package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val accountVerified: Boolean,
    val registrationDate: Instant,
    // TODO: лучше использовать String, чтобы не было проблем с сериализацией
    val balanceAmount: String?,
)
