package ru.itmo.analytics.server.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ru.itmo.analytics.server.model.entity.AnalyticsBetsEntity
import ru.itmo.analytics.server.repository.AnalyticsBetsRepository
import ru.itmo.common.dto.BetCreatedEventDto
import java.time.Instant

@Service
class BetsCreatedService(
    private val analyticsBetsRepository: AnalyticsBetsRepository
) {

    @KafkaListener(
        topics = ["bets-created"],
        groupId = "analytics-consumer-group"
    )
    fun handleBetCreated(msg: BetCreatedEventDto) {
        analyticsBetsRepository.save(
            AnalyticsBetsEntity(
                betId = msg.betId,
                ts = Instant.now(),
                userId = msg.userId,
                matchId = msg.matchId,
                matchName = msg.matchName,
                betAmount = msg.betAmount,
            )
        )
    }
}