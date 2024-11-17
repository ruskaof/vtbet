package ru.itmo.auth.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.itmo.common.exception.DuplicateException
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.itmo.common.response.VtbetExceptionResponse
import org.springframework.http.HttpStatus
import ru.itmo.common.exception.AuthException

@RestControllerAdvice
class MethodArgumentNotValidExceptionAdvice {

    @ExceptionHandler(DuplicateException::class)
    fun handleDuplicateException(e: DuplicateException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.message ?: "Duplicate entry"
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity(VtbetExceptionResponse(status.value(), message), status)
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(e: AuthException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.message ?: "Auth error"
        val status = HttpStatus.UNAUTHORIZED
        return ResponseEntity(VtbetExceptionResponse(status.value(), message), status)
    }
}