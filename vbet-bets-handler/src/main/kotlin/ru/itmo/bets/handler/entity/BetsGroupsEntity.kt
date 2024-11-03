package ru.itmo.bets.handler.entity

import jakarta.persistence.*

@Entity(name = "bets_groups")
class BetsGroupsEntity(
    @Id
    @Column(name = "group_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val groupId: Long? = null,
    @Column(name = "description", nullable = false)
    var description: String,
)
