package ru.itmo.user.accounter.model.entity

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "users")
data class UsersEntity(
    @Id
    @Column("user_id")
    val userId: Long? = null,
    @Column("username")
    var username: String,
    @Column("email")
    @field:Email(message = "must be valid email")
    var email: String? = null,
    @Column("phone_number")
    @field:Pattern(regexp = "[0-9]{10}", message = "Invalid phone number")
    var phoneNumber: String? = null,
    @Column("account_verified")
    var accountVerified: Boolean,
    @Column("registration_date")
    val registrationDate: Instant,
)
