package es.in2.wallet.integration.orion.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

class VerifiableCredentialEntity(
    @JsonProperty("id") val id: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("userId") @JsonAlias("user_id") val userId: OrionAttribute,
    @JsonProperty("vcData") @JsonAlias("vc_data") val vcData: OrionAttribute,
)



