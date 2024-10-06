package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "matches")
class MatchesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id", nullable = false)
    val matchId: Long? = null,
    @Column(name = "match_name", nullable = false)
    val matchName: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    val sportEntity: SportEntity,
)
