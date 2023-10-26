package es.in2.wallet.api.exception.handler

import es.in2.wallet.api.exception.EmailAlreadyExistsException
import es.in2.wallet.api.exception.NoSuchQrContentException
import es.in2.wallet.api.exception.NoSuchVerifiableCredentialException
import es.in2.wallet.api.exception.UsernameAlreadyExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(EmailAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmailAlreadyExistsException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UsernameAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUsernameAlreadyExistsException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchQrContentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNoSuchQrContentException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchVerifiableCredentialException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchVerifiableCredentialException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
}