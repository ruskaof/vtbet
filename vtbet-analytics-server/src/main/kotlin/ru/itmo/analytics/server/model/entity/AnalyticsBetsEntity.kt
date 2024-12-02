package ru.itmo.analytics.server.model.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity(name = "analytics_bets")
class AnalyticsBetsEntity(
    @Id
    @Column(name = "bet_id")
    val betId: Long,
    @Column(name = "ts")
    val ts: Instant,
    @Column(name = "user_id")
    val userId: Long,
    @Column(name = "match_id")
    val matchId: Long,
    @Column(name = "match_name")
    val matchName: String,
    @Column(name = "bet_amount")
    val betAmount: BigDecimal,
)
