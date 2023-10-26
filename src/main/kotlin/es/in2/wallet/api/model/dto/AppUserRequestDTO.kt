package es.in2.wallet.api.model.dto

data class AppUserRequestDTO(
    val username: String,
    val email: String,
    val password: String,
)