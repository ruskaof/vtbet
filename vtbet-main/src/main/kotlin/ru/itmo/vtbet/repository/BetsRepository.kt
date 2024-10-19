package ru.itmo.vtbet.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.BetsEntity

interface BetsRepository : JpaRepository<BetsEntity, Long> {
    fun findAllByAvailableBetIdIn(availableBetIds: List<Long>): List<BetsEntity>
    fun findAllByUsersEntityUserId(userId: Long, of: PageRequest): Page<BetsEntity>
}
