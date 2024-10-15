package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import java.time.Instant

@Entity(name = "users")
data class UsersEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    val userId: Long? = null,
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
    @Column(name = "registration_date", nullable = false)
    val registrationDate: Instant,
)
