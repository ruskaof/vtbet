package ru.itmo.vtbet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VtbetApplication

fun main(args: Array<String>) {
    runApplication<VtbetApplication>(*args)
}
