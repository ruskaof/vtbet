package ru.itmo.vtbet.model.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity(name = "type_of_bet")
class TypeOfBetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val typeOfBetId: Long? = null,
    @Column(name = "description", nullable = false)
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val description: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_group_id")
    val betGroupEntity: BetGroupEntity,
) {
}