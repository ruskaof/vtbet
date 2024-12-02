package ru.itmo.analytics.server.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.itmo.analytics.server.model.entity.AnalyticsBetsEntity

@Repository
interface AnalyticsBetsRepository: CrudRepository<AnalyticsBetsEntity, Long>
