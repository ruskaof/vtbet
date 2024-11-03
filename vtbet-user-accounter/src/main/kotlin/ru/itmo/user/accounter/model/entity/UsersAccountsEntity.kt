package ru.itmo.user.accounter.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table(name = "users_accounts")
data class UsersAccountsEntity(
    @Id
    @Column("account_id")
    val accountId: Long? = null,
    @Column("balance_amount")
    var balanceAmount: BigDecimal,
    @Column("user_id")
    val userId: Long,
)
