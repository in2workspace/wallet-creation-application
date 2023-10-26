package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

class VcBasicDataDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("vcType") val vcType: MutableList<String>,
    @JsonProperty("credentialSubject") val credentialSubject: Any
)