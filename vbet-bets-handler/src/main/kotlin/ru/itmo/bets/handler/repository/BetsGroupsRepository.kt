package ru.itmo.bets.handler.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.bets.handler.entity.BetsGroupsEntity

interface BetsGroupsRepository : JpaRepository<BetsGroupsEntity, Long>
