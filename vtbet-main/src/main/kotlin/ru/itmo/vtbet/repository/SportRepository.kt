package ru.itmo.vtbet.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.vtbet.model.entity.SportEntity
import java.util.*


interface SportRepository : JpaRepository<SportEntity, Long> {
    fun findBySportName(sportName: String): Optional<SportEntity>
}