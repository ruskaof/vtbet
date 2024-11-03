package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity(name = "available_bets")
data class AvailableBetsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_bet_id", nullable = false)
    val availableBetId: Long? = null,
    @Column(name = "ratio", nullable = false)
    @field:PositiveOrZero(message = "ratio must be positive or zero")
    val ratio: BigDecimal,
    @Column(name = "bets_closed", nullable = false)
    val betsClosed: Boolean,
    @Column(name = "match_id", nullable = false)
    val matchId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val betsGroupsEntity: BetsGroupsEntity,
)
