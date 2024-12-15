package ru.itmo.analytics.server.kafka

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.any
import ru.itmo.analytics.server.model.entity.AnalyticsBetsEntity
import ru.itmo.analytics.server.repository.AnalyticsBetsRepository
import ru.itmo.common.dto.BetCreatedEventDto
import java.math.BigDecimal

class BetsCreatedServiceTest {

    @Test
    fun handleBetCreated() {
        val repository = Mockito.mock(AnalyticsBetsRepository::class.java)
        val betsCreatedService = BetsCreatedService(repository)

        Mockito.`when`(repository.save(any())).thenReturn(Mockito.mock(AnalyticsBetsEntity::class.java))
        betsCreatedService.handleBetCreated(BetCreatedEventDto(1, 1, BigDecimal.ONE, 1, "test"))

        Mockito.verify(repository).save(any())
    }
}