package ru.itmo.sports.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.sports.model.entity.SportsEntity

interface SportsRepository : JpaRepository<SportsEntity, Long>
