package ru.itmo.vtbet.controller

import jakarta.persistence.PersistenceException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.itmo.vtbet.exception.IllegalBetActionException
import ru.itmo.vtbet.exception.ResourceNotFoundException

@RestControllerAdvice
class MethodArgumentNotValidExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): String {
        val message = e.bindingResult.fieldErrors.map { it.defaultMessage }.joinToString(", ")
        return "Invalid request: $message"
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleMethodArgumentNotValidException(e: ConstraintViolationException): String {
        val message = e.constraintViolations.joinToString(", ") { it.message }
        return "Invalid data: $message, ${e.message}"
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): String {
        return e.message ?: "Could not find requested resource"
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalBetActionException::class)
    fun handleResourceNotFoundException(e: IllegalBetActionException): String {
        return e.message ?: "Illegal bet"
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PersistenceException::class)
    fun handleDbException(e: PersistenceException): String {
        return "Could not process request because of database error: ${e.message}"
    }
}
