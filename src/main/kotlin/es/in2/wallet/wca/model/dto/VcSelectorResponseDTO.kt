package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

class VcSelectorResponseDTO(
    @JsonProperty("redirectUri") val redirectUri: String,
    @JsonProperty("state") val state: String,
    @JsonProperty("selectedVcList") val selectedVcList: List<VcBasicDataDTO>,
)