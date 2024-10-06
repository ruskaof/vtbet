package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "matches")
class MatchesEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(nullable = false)
        val matchName: String,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "sport_id")
        val sportEntity: SportEntity,
) {
}