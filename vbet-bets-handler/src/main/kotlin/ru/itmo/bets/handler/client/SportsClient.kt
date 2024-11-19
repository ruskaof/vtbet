package ru.itmo.bets.handler.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.itmo.common.response.MatchResponse
import ru.itmo.common.response.SportResponse
import java.util.logging.Level
import java.util.logging.Logger


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
        Logger.getLogger("sports-client").log(Level.WARNING) { "Using fallback method on get match" }
        return MatchResponse(-1, "Stub match", SportResponse(-1, "Stub sport"), false)
    }

    override fun endMatch(id: Long) {
        Logger.getLogger("sports-client").log(Level.WARNING) { "Using fallback method on end match" }
    }
}
