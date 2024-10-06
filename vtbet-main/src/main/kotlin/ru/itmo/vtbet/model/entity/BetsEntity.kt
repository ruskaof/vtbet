package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "bets")
class BetsEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(nullable = false)
        val amount: Double,
        @Column(nullable = false)
        val ratio: Double,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "user_id")
        val usersEntity: UsersEntity,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "type_of_bet_match_id")
        val typeOfBetMatchEntity: TypeOfBetMatchEntity,
) {

}