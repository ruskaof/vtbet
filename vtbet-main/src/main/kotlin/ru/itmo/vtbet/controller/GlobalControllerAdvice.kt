package ru.itmo.vtbet.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class MethodArgumentNotValidExceptionAdvice {

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException::class)
        fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): String {
            val message = e.bindingResult.fieldErrors.map { it.defaultMessage }.joinToString(", ")
            return message
        }
}