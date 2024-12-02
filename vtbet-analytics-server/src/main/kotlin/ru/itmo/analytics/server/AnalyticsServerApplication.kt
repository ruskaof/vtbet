package ru.itmo.analytics.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnalyticsServer

fun main(args: Array<String>) {
    runApplication<AnalyticsServer>(*args)
}