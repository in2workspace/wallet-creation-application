package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QrContentDTO (
    @JsonProperty("qr_content") val content: String
)