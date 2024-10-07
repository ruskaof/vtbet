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

@Entity(name = "type_of_bet_match")
data class TypeOfBetMatchEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "ratio_now", nullable = false)
    @field:PositiveOrZero(message = "ratio must be positive or zero")
    val ratioNow: BigDecimal,
    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    val match: MatchesEntity,
    @ManyToOne(optional = false)
    @JoinColumn(name = "type_of_bet_id")
    val typeOfBets: TypeOfBetEntity,
)
