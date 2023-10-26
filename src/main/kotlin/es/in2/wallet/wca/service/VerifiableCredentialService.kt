package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.dto.CredentialRequestDTO

interface VerifiableCredentialService {
    fun getCredentialIssuerMetadata(credentialOfferUriExtended: String)
    fun getVerifiableCredential(credentialRequestDTO: CredentialRequestDTO)
}