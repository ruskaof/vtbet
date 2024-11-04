package ru.itmo.configs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class ConfigsApplication

fun main(args: Array<String>) {
    runApplication<ConfigsApplication>(*args)
}