package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity(name = "available_bet")
data class AvailableBetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_of_bet_match_id")
    val id: Long? = null,
    @Column(name = "ratio_now", nullable = false)
    @field:PositiveOrZero(message = "ratio must be positive or zero")
    val ratioNow: BigDecimal,
    @Column(name = "bets_closed", nullable = false)
    val betsClosed: Boolean,
    @Column(name = "match_id", nullable = false)
    val matchId: Long,
    @Column(name = "type_of_bet_id", nullable = false)
    val typeOfBetId: Long,
)
