package ru.itmo.sports.ru.itmo.sports.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ru.itmo.common.response.VtbetExceptionResponse

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders?,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        return Mono.just(ResponseEntity(VtbetExceptionResponse(status.value(), ex.message ?: "Unknown error"), status))
    }
}