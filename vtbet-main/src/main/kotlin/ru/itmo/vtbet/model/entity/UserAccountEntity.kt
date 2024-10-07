package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal

@Entity(name = "user_account")
data class UserAccountEntity(
    @Id
    val userId: Long,
    @Column(name = "balance_amount", nullable = false)
    var balanceAmount: BigDecimal,
    @Column(name = "username", nullable = false)
    var username: String,
    @Column(name = "email", nullable = true)
    @field:Email(message = "must be valid email")
    var email: String? = null,
    @Column(name = "phone_number", nullable = true)
    @field:Pattern(regexp = "[0-9]{10}", message = "Invalid phone number")
    var phoneNumber: String? = null,
    @Column(name = "account_verified", nullable = false)
    var accountVerified: Boolean,
)
