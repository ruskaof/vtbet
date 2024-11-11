package ru.itmo.common.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal
import java.time.Instant

@Table(name = "users_accounts")
data class UsersAccountsEntity(
    @Id
    @Column(name = "user_id")
    val userId: Long? = null,
    @Column(name = "balance_amount")
    var balanceAmount: BigDecimal,
    @Column(name = "email")
    @field:Email(message = "must be valid email")
    var email: String? = null,
    @Column(name = "phone_number")
    @field:Pattern(regexp = "[0-9]{10}", message = "Invalid phone number")
    var phoneNumber: String? = null,
    @Column(name = "account_verified")
    var accountVerified: Boolean,
    @Column(name = "registration_date")
    val registrationDate: Instant
)
