package ru.itmo.user.accounter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserAccounterApplication

fun main(args: Array<String>) {
    runApplication<UserAccounterApplication>(*args)
}