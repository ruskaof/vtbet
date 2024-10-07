package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Size

@Entity(name = "sport")
data class SportEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id", nullable = false)
    val sportId: Long? = null,
    @Column(name = "sport_name", nullable = false)
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val sportName: String,
)
