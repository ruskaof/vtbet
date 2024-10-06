package ru.itmo.vtbet.model.entity

import jakarta.persistence.*

@Entity(name = "type_of_bet")
class TypeOfBetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val typeOfBetId: Long? = null,
    @Column(name = "description", nullable = false)
    val description: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_group_id")
    val betGroupEntity: BetGroupEntity,
) {
}