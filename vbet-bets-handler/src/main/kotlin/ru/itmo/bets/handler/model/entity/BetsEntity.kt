package ru.itmo.bets.handler.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "bets")
class BetsEntity(
    @Id
    @Column(name = "bet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val betId: Long? = null,
    @Column(name = "user_id")
    val userId: Long,
    @Column(nullable = false)
    val amount: BigDecimal,
    @Column(nullable = false)
    val ratio: BigDecimal,
    @Column(name = "available_bet_id", nullable = false)
    val availableBetId: Long,
)
