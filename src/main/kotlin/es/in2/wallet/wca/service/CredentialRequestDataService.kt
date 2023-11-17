package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.entity.CredentialRequestData
import java.util.*

interface CredentialRequestDataService {
    fun saveCredentialRequestData(issuerName: String, issuerNonce: String, issuerAccessToken: String, userId: String)
    fun getCredentialRequestDataByIssuerName(issuerName: String, userId: String) : Optional<CredentialRequestData>
    fun saveNewIssuerNonceByIssuerName(issuerName: String, freshNonce: String, userId: String)
}