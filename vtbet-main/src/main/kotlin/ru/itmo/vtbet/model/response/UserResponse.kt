package ru.itmo.vtbet.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserResponse(
    val id: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
)
