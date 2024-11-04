package ru.itmo.bets.handler.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import ru.itmo.bets.handler.model.response.SportResponse
import ru.itmo.vtbet.model.response.MatchResponse

@FeignClient(name = "sports")
interface SportsClient {

    @GetMapping("/sports/{id}")
    fun getSportsById(id: Int): SportResponse // fixme use sport response from common

    @GetMapping("/matches/{id}")
    fun getMatch(id: Long): MatchResponse // fixme use match response from common

    @PostMapping("/matches/{id}/ended")
    fun endMatch(id: Long)
}