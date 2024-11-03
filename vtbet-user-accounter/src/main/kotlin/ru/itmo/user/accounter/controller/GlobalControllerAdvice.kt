package ru.itmo.user.accounter.controller

import io.r2dbc.spi.R2dbcException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.itmo.common.exception.DuplicateException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.exception.VtbetExceptionResponse

@RestControllerAdvice
class MethodArgumentNotValidExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.bindingResult.fieldErrors.map { it.defaultMessage }.joinToString(", ")
        return ResponseEntity(VtbetExceptionResponse(HttpStatus.BAD_REQUEST.value(), message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleMethodArgumentNotValidException(e: ConstraintViolationException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.message.orEmpty() + " " + e.constraintViolations.joinToString(", ") { it.message }
        return ResponseEntity(VtbetExceptionResponse(HttpStatus.BAD_REQUEST.value(), message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.message ?: "Could not find requested resource"
        val status = HttpStatus.NOT_FOUND
        return ResponseEntity(VtbetExceptionResponse(status.value(), message), status)
    }

    @ExceptionHandler(DuplicateException::class)
    fun handleDuplicateException(e: DuplicateException): ResponseEntity<VtbetExceptionResponse> {
        val message = e.message ?: "Duplicate entry"
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity(VtbetExceptionResponse(status.value(), message), status)
    }

    @ExceptionHandler(R2dbcException::class)
    fun handleDbException(e: R2dbcException): ResponseEntity<VtbetExceptionResponse> {
        val message = "Could not process request because of database error: ${e.message}"
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(VtbetExceptionResponse(status.value(), message), status)
    }
}
