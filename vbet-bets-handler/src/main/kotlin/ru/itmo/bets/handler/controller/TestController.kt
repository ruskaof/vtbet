package ru.itmo.bets.handler.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.bets.handler.kafka.KafkaProducer
import ru.itmo.common.dto.BetCreatedEventDto
import java.math.BigDecimal

@RestController
class TestController(
    private val kafkaProducer: KafkaProducer
) {

    @PostMapping("/test")
    fun test() {
        kafkaProducer.produceBetCreated(BetCreatedEventDto(1, 1, BigDecimal.TWO, 1, "test"))
    }
}