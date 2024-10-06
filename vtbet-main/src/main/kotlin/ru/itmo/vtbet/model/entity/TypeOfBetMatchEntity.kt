package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "type_of_bet_match")
class TypeOfBetMatchEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "ratio_now", nullable = false)
    val ratioNow: BigDecimal,
    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    val matchesEntity: MatchesEntity,
    @ManyToOne(optional = false)
    @JoinColumn(name = "type_of_bet_id")
    val typeOfBetEntity: TypeOfBetEntity,
) {
}