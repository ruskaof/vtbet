package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "type_of_bet_match")
class TypeOfBetMatchEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(nullable = false)
        val ratioNow: Double,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "match_id")
        val matchesEntity: MatchesEntity,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "type_of_bed_id")
        val typeOfBetEntity: TypeOfBetEntity,
) {
}