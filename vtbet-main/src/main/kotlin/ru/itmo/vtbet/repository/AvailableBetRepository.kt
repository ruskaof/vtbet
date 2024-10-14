package ru.itmo.vtbet.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.AvailableBetEntity

interface AvailableBetRepository : JpaRepository<AvailableBetEntity, Long> {
    fun findAllByMatchMatchId(matchId: Long, pageable: Pageable): Page<AvailableBetEntity>
    fun findAllByMatchMatchId(matchId: Long): List<AvailableBetEntity>
}
