package es.in2.wallet.controller

import es.in2.wallet.api.exception.*
import es.in2.wallet.api.exception.handler.ApiExceptionHandler
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
class ApiExceptionHandlerTest {

    @Test
    fun testHandleEmailAlreadyExistsException() {
        val exception = EmailAlreadyExistsException("Email already exists")
        val response = ApiExceptionHandler().handleEmailAlreadyExistsException(exception)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun testHandleUsernameAlreadyExistsException() {
        val exception = UsernameAlreadyExistsException("Username already exists")
        val response = ApiExceptionHandler().handleUsernameAlreadyExistsException(exception)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun testHandleNoSuchQrContentException() {
        val exception = NoSuchQrContentException("Unknown QR content")
        val response = ApiExceptionHandler().handleNoSuchQrContentException(exception)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun testHandleNoSuchVerifiableCredentialException() {
        val exception = NoSuchVerifiableCredentialException("There are no Verifiable Credential bound to the user")
        val response = ApiExceptionHandler().handleNoSuchVerifiableCredentialException(exception)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}