package ru.itmo.bets.handler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.cloud.openfeign.EnableFeignClients


@SpringBootApplication
@EnableFeignClients
@EnableHystrix
class BetsApplication

fun main(args: Array<String>) {
    runApplication<BetsApplication>(*args)
}