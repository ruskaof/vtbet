package ru.itmo.bets.handler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class DuplicateException(message: String) : Exception(message)