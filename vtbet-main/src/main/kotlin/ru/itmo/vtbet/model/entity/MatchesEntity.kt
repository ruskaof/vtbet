package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.Size

@Entity(name = "matches")
data class MatchesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id", nullable = false)
    val matchId: Long? = null,
    @Column(name = "match_name", nullable = false)
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val matchName: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    val sportEntity: SportEntity,
    @Column(name = "ended", nullable = false)
    val ended: Boolean
)
