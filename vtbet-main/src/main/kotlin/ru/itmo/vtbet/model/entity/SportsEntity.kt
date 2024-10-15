package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity(name = "sports")
data class SportsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id", nullable = false)
    val sportId: Long? = null,
    @Column(name = "sport_name", nullable = false)
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val sportName: String,
)
