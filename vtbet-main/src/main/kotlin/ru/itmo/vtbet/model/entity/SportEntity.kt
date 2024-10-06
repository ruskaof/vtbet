package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "sport")
class SportEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(nullable = false)
        val sportName: String,
) {
}