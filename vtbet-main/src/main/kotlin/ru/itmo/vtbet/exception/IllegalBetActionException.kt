package ru.itmo.vtbet.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class IllegalBetActionException(message: String) : Exception(message)