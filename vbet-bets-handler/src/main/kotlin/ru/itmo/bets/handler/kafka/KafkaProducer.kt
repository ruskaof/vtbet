package ru.itmo.bets.handler.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import ru.itmo.common.dto.BetCreatedEventDto

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {

    fun produceBetCreated(betCreatedEventDto: BetCreatedEventDto) {
        kafkaTemplate.send("bets-created", betCreatedEventDto).join()
    }
}