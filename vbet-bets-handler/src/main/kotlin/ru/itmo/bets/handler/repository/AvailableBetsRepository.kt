package ru.itmo.bets.handler.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.common.entity.AvailableBetsEntity

interface AvailableBetsRepository : JpaRepository<AvailableBetsEntity, Long> {
    fun findAllByMatchId(matchId: Long, pageable: Pageable): Page<AvailableBetsEntity>
    fun findAllByMatchId(matchId: Long): List<AvailableBetsEntity>
}
