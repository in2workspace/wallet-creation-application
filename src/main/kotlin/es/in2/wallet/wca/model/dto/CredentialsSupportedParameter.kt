package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import id.walt.credentials.w3c.templates.VcTemplate

data class CredentialsSupportedParameter(
    @JsonProperty("format") val format: String,
    @JsonProperty("id") val id: String,
    @JsonProperty("types") val types: MutableList<String>,
    @JsonProperty("cryptographic_binding_methods_supported") val cryptographicBindingMethodsSupported: MutableList<String>,
    @JsonProperty("cryptographic_suites_supported") val cryptographicSuitesSupported: MutableList<String>,
    @JsonProperty("credentialSubject") val credentialSubject: VcTemplate,
)
