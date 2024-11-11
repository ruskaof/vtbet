package ru.itmo.common.entity

import jakarta.persistence.*

@Entity(name = "roles")
class RolesEntity(
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val roleId: Long? = null,
    @Column(name = "name")
    val name: String,
)
