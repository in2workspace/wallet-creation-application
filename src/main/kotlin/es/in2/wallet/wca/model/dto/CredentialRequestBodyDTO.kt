package es.in2.wallet.wca.model.dto

data class CredentialRequestBodyDTO(
        val format: String,
        val proof: ProofDTO
)
