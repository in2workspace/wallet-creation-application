package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

class SelectableVCsRequestDTO(
    @JsonProperty("vcTypes") val vcTypes: List<String>
)