package ru.itmo.bets.handler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients


@SpringBootApplication
@EnableFeignClients
class SportsApplication

fun main(args: Array<String>) {
    runApplication<SportsApplication>(*args)
}