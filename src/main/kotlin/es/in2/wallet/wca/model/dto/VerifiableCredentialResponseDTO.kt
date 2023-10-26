package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = """
    Implements the credential response according to 
    https://github.com/hesusruiz/EUDIMVP/blob/main/issuance.md#credential-response
    """)
data class VerifiableCredentialResponseDTO(
    @Schema(
        required = true,
        example = "jwt_vc_json",
        description = "Format of the issued Credential.")
    @param:JsonProperty("format") @get:JsonProperty("format")
    val format: String,

    @Schema(
        required = true,
        example = "LUpixVCWJk0eOt4CXQe1NXK....WZwmhmn9OQp6YxX0a2L",
        description = "Contains issued Credential")
    @param:JsonProperty("credential") @get:JsonProperty("credential")
    val credential: String,

    @Schema(
        required = false,
        example = "fGFF7UkhLA",
        description = """
            Nonce to be used to create a proof of possession of key material when requesting a Credential. 
            When received, the Wallet MUST use this nonce value for its subsequent credential requests until the 
            Credential Issuer provides a fresh nonce.
        """)
    @param:JsonProperty("c_nonce") @get:JsonProperty("c_nonce")
    val cNonce: String,

    @Schema(
        required = false,
        example = "86400",
        description = "Lifetime in seconds of the c_nonce")
    @param:JsonProperty("c_nonce_expires_in") @get:JsonProperty("c_nonce_expires_in")
    val cNonceExpiresIn: Int
)