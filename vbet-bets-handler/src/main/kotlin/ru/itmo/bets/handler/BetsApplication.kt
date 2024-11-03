package ru.itmo.bets.handler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SportsApplication

fun main(args: Array<String>) {
    runApplication<SportsApplication>(*args)
}