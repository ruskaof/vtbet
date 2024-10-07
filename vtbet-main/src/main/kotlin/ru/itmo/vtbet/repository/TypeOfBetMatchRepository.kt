package ru.itmo.vtbet.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity

interface TypeOfBetMatchRepository : JpaRepository<TypeOfBetMatchEntity, Long> {
    fun findAllByMatchMatchId(matchId: Long, pageable: Pageable): Page<TypeOfBetMatchEntity>
    fun findAllByMatchMatchId(matchId: Long): List<TypeOfBetMatchEntity>
}
