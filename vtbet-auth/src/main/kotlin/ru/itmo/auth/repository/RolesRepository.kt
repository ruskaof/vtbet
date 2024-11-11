package ru.itmo.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.common.entity.RolesEntity

interface RolesRepository: JpaRepository<RolesEntity, Long> {
    fun findByName(name: String): RolesEntity
}
