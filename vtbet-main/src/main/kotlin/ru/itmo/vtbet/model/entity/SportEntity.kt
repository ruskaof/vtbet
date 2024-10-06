package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "sport")
class SportEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id", nullable = false)
    val sportId: Long? = null,
    @Column(name = "sport_name", nullable = false)
    val sportName: String,
) {
}