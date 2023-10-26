package es.in2.wallet.api.service

import es.in2.wallet.api.model.entity.Issuer
import java.util.*

interface IssuerService {
    fun upsertIssuerData(issuerName: String, issuerMetadata: String)
    fun getIssuerByName(issuerName: String): Optional<Issuer>
    fun getIssuers(): List<String>
    fun getMetadata(issuerName: String): String
}