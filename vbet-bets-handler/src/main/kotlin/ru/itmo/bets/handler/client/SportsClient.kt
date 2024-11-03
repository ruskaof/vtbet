package ru.itmo.bets.handler.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.itmo.common.response.MatchResponse

@FeignClient(name = "user-sports-client")
interface SportsClient {
    @GetMapping("/matches/{id}")
    fun getMatch(@PathVariable id: Long): MatchResponse

    @PostMapping("/sports/{id}/status")
    fun endMatch(@PathVariable id: Long, @RequestBody isFinished: Boolean): Unit
}