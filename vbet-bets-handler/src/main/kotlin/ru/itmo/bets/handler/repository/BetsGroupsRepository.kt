package ru.itmo.bets.handler.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.common.entity.BetsGroupsEntity

interface BetsGroupsRepository : JpaRepository<BetsGroupsEntity, Long>
