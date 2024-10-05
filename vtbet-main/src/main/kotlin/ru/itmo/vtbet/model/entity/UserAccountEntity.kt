package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity(name = "user_account")
class UserAccountEntity(
    @Id
    val userId: Long,
    @Column(name = "balance_amount", nullable = false)
    val balanceAmount: BigDecimal,
    @Column(name = "username", nullable = false)
    val username: String,
    @Column(name = "email", nullable = true)
    val email: String? = null,
    @Column(name = "phone_number", nullable = true)
    val phoneNumber: String? = null,
    @Column(name = "account_verified", nullable = false)
    val accountVerified: Boolean,
)
