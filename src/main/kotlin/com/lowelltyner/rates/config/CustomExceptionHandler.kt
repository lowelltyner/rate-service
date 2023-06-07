package com.lowelltyner.rates.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private const val UNAVAILABLE = "unavailable"

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // Ensures Swagger documents this error response
    fun customHandlingOfException(e: Exception, request: WebRequest): ResponseEntity<String> {
        logger.warn("Exception encountered, returning default response: $UNAVAILABLE", e)
        return ResponseEntity(UNAVAILABLE, HttpStatus.BAD_REQUEST)
    }
}