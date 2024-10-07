package ru.itmo.vtbet.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.BetsEntity

interface BetsRepository : JpaRepository<BetsEntity, Long> {
    fun findByTypeOfBetMatchTypeOfBetMatchIdIn(typeOfBetMatchIds: List<Long>): List<BetsEntity>
}
