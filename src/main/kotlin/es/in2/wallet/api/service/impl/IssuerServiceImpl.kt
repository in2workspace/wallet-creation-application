package es.in2.wallet.api.service.impl

import es.in2.wallet.api.model.repository.IssuerRepository
import es.in2.wallet.api.exception.IssuerNotFoundException
import es.in2.wallet.api.model.entity.Issuer
import es.in2.wallet.api.service.IssuerService
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Service
class IssuerServiceImpl(
    private val issuerRepository: IssuerRepository,
) : IssuerService {

    private val log: Logger = LoggerFactory.getLogger(AppUserServiceImpl::class.java)

    /**
     * Inserts or updates Credential Issuer Metadata on an SQL table
     */
    override fun upsertIssuerData(issuerName: String, issuerMetadata: String) {
        val issuerData = getIssuerByName(issuerName)
        val issuer: Issuer = if (issuerData.isPresent) {
            Issuer(
                id = issuerData.get().id,
                name = issuerName,
                metadata = issuerMetadata
            )
        } else {
            Issuer(
                id = null,
                name = issuerName,
                metadata = issuerMetadata
            )
        }
        issuerRepository.save(issuer)
    }

    override fun getIssuerByName(issuerName: String): Optional<Issuer> {
        log.info("AppIssuerDataServiceImpl.getUserByUsername()")
        return issuerRepository.findAppIssuerDataByName(issuerName)
    }

    override fun getIssuers(): List<String> {
        log.info("AppIssuerServiceImpl.getIssuers()")
        val issuers = issuerRepository.findAll()
        val issuerResponseList = issuers.map { issuer ->
            issuer.name
        }
        if (issuerResponseList.isEmpty()) {
            throw IssuerNotFoundException("The Issuer List is empty.")
        }
        return issuerResponseList
    }

    override fun getMetadata(issuerName: String): String {
        val issuerData = getIssuerByName(issuerName)
        if (issuerData.isPresent){
            return issuerData.get().metadata
        } else {
            throw IssuerNotFoundException("Issuer metadata not found for $issuerName")
        }
    }
}