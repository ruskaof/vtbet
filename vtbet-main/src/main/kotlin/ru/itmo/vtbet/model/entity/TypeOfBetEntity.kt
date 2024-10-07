package ru.itmo.vtbet.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.Size

@Entity(name = "type_of_bet")
data class TypeOfBetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val typeOfBetId: Long? = null,
    @Column(name = "description", nullable = false)
    @field:Size(max = 255, min = 1, message = "String length 1 and 255")
    val description: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_group_id")
    val betGroupEntity: BetGroupEntity,
)
