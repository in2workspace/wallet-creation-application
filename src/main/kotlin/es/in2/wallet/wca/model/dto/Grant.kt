package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class Grant(
    @Schema(
        required = true,
        example = "1234")
    @param:JsonProperty("pre-authorized_code") @get:JsonProperty("pre-authorized_code")
    val preAuthorizedCode: String,

    @Schema(
        required = true,
        example = "true")
    @param:JsonProperty("user_pin_required") @get:JsonProperty("user_pin_required")
    val userPinRequired: Boolean,
)