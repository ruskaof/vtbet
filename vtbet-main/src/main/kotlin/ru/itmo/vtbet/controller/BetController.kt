package ru.itmo.vtbet.controller;

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.service.BetService

@RestController
@Validated
class BetController(
    private val betService: BetService,
) {


}
