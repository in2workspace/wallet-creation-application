package es.in2.wallet.api.security.exception

import org.springframework.security.core.AuthenticationException

class AccessTokenException(override var message: String) : AuthenticationException(message)