package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CredentialIssuerMetadata(
    @JsonProperty("credential_issuer") val credentialIssuer: String,
    @JsonProperty("credential_endpoint") val credentialEndpoint: String,
    @JsonProperty("credential_token") val credentialToken: String,
    @JsonProperty("credentials_supported") val credentialsSupported: MutableList<CredentialsSupportedParameter>
)
