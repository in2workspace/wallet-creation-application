package es.in2.wallet.api.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QrContentDTO (
    @JsonProperty("qr_content") val content: String
)