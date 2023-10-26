package es.in2.wallet.api.model.dto

data class OpenIdConfig(
    val scope: String,
    val responseType: String,
    val responseMode: String,
    val clientId: String,
    val redirectUri: String,
    val state: String,
    val nonce: String
)