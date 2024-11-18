package ru.itmo.bets.handler.client

import jakarta.servlet.UnavailableException
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.itmo.common.response.MatchResponse


@FeignClient(name = "sports-client", fallback = Fallback::class)
interface SportsClient {
    @GetMapping("/matches/{id}")
    fun getMatch(@PathVariable id: Long): MatchResponse

    @PostMapping("/matches/{id}/ended")
    fun endMatch(@PathVariable id: Long): Unit
}

@Component
class Fallback : SportsClient {
    override fun getMatch(id: Long): MatchResponse {
        throw UnavailableException("Sports is currently unavailable")
    }

    override fun endMatch(id: Long) {
        throw UnavailableException("Sports is currently unavailable")
    }
}
