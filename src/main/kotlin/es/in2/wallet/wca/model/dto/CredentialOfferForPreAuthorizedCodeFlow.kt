package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = """
   This data class is used to represent the Credential Offer by Reference using credential_offer_uri parameter for a
   Pre-Authorized Code Flow. 
   For more information: https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-sending-credential-offer-by-
""")
data class CredentialOfferForPreAuthorizedCodeFlow(
    @Schema(
        required = true,
        example = "https://credential-issuer.example.com")
    @param:JsonProperty("credential_issuer") @get:JsonProperty("credential_issuer")
    val credentialIssuer: String,

    @Schema(
        required = true,
        example = "[\"UniversityDegree\"]")
    @param:JsonProperty("credentials") @get:JsonProperty("credentials")
    val credentials: MutableList<String>,

    //TODO: The key should not be string but a specific keyword like "urn:ietf:params:oauth:grant-type:pre-authorized_code"
    @Schema(implementation = Grant::class)
    @param:JsonProperty("grants") @get:JsonProperty("grants")
    val grants: MutableMap<String, Grant>
)
