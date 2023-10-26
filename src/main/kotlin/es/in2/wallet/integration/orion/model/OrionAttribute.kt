package es.in2.wallet.integration.orion.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.JsonNodeFactory

data class OrionAttribute(
    @JsonProperty("type") val type: String,
    @JsonProperty("value") val value: Any,
    @JsonProperty("metadata") val metadata: Any? = JsonNodeFactory.instance.objectNode(),
)
