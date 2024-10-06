package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "type_of_bet")
class TypeOfBetEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(nullable = false)
        val description: String,
        @Column(nullable = false)
        @ManyToOne
        @JoinColumn(name = "bet_group_id")
        val betGroupEntity: BetGroupEntity,
) {
}