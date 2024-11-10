package ru.itmo.auth.model.entity

import jakarta.persistence.*

@Entity(name = "users")
class UsersEntity (
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long? = null,
    @Column(name = "username")
    val username: String,
    @Column(name = "password")
    val password: String,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    val roles: Set<RolesEntity>,
)
