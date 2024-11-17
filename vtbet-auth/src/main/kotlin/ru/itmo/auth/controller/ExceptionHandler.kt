package ru.itmo.auth.controller

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.itmo.common.response.VtbetExceptionResponse
import java.lang.Exception

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(
        ex: Exception,
        @Nullable body: Any?,
        headers: org.springframework.http.HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return ResponseEntity(VtbetExceptionResponse(statusCode.value(), ex.message ?: "Unknown error"), statusCode)
    }
}
