package ru.itmo.vtbet.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity


interface TypeOfBetMatchRepository : JpaRepository<TypeOfBetMatchEntity, Long> {
}