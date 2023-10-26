package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.entity.CredentialRequestData
import java.util.*

interface CredentialRequestDataService {
    fun saveCredentialRequestData(issuerName: String, issuerNonce: String, issuerAccessToken: String)
    fun getCredentialRequestDataByIssuerName(issuerName: String) : Optional<CredentialRequestData>
    fun saveNewIssuerNonceByIssuerName(issuerName: String, freshNonce: String)
}