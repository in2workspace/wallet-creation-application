package es.in2.wallet.integration.orion.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

class DidEntity(
    @JsonProperty("id") val id: String, // We use the did directly on the id
    @JsonProperty("type") val type: String,
    @JsonProperty("userId") @JsonAlias("user_id") val userId: OrionAttribute, // We associate with userId for further queries
)