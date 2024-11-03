package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "users_accounts")
data class UsersAccountsEntity(
    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val accountId: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val usersEntity: UsersEntity,
    @Column(name = "balance_amount", nullable = false)
    var balanceAmount: BigDecimal,
)
