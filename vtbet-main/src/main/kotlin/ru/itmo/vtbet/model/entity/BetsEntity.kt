package ru.itmo.vtbet.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "bets")
class BetsEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
//        @Column(nullable = false)
//        val amount: BigDecimal,
//        @Column(nullable = false)
//        val ratio: BigDecimal,
//        @Column(nullable = false)
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "user_id")
//        val usersEntity: UsersEntity,
//        @Column(nullable = false)
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "type_of_bet_match_id")
//        val typeOfBetMatchEntity: TypeOfBetMatchEntity,
) {
}