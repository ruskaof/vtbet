package ru.itmo.user.accounter.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority

@Table(name = "roles")
data class RolesEntity(
    @Id
    @Column("role_id")
    val roleId: Long,
    @Column("role_name")
    val roleName: String,
): GrantedAuthority {
    override fun getAuthority(): String {
        return roleName
    }
}