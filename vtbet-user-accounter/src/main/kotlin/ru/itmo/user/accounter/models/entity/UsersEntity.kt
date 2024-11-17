package ru.itmo.user.accounter.models.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table(name = "users")
data class UsersEntity (
    @Column("user_id")
    val userId: Long? = null,
    @Column("username")
    val username: String,
    @Column("password")
    val password: String?,
)
