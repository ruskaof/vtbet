package ru.itmo.vtbet.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.MatchesEntity


interface MatchesRepository : JpaRepository<MatchesEntity, Long> {
    fun findBySportEntity(sportId: Long, pageable: Pageable): Page<MatchesEntity>
    override fun findAll(pageable: Pageable): Page<MatchesEntity>
}
