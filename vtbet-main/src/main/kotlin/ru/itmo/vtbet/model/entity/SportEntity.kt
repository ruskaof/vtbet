package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "sport")
class SportEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val sport_id: Long? = null,
        @Column(nullable = false)
        val sportName: String,
) {
}