package es.in2.wallet.api.service.impl

import es.in2.wallet.api.exception.NoSuchQrContentException
import es.in2.wallet.api.model.entity.QrType
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.api.service.QrCodeProcessorService
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.VerifiableCredentialService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

@Service
class QrCodeProcessorServiceImpl(
    private val siopService: SiopService,
    private val verifiableCredentialService: VerifiableCredentialService,
    private val orionService: OrionService,
) : QrCodeProcessorService {

    private val log: Logger = LogManager.getLogger(QrCodeProcessorServiceImpl::class.java)

    override fun processQrContent(qrContent: String): Any {

        log.debug("Processing QR content: $qrContent")

        return when (identifyQrContentType(qrContent)) {
            QrType.SIOP_AUTH_REQUEST_URI -> {
                log.info("Processing SIOP authentication request URI")
                siopService.getSiopAuthenticationRequest(qrContent)
            }

            QrType.SIOP_AUTH_REQUEST -> {
                log.info("Processing SIOP authentication request")
                siopService.processSiopAuthenticationRequest(qrContent)
            }

            QrType.CREDENTIAL_OFFER_URI -> {
                log.info("Processing verifiable credential offer URI")
                verifiableCredentialService.getCredentialIssuerMetadata(qrContent)
            }

            QrType.VC_JWT -> {
                log.info("Saving verifiable credential in VC JWT format")
                orionService.saveVC(qrContent)
            }

            QrType.UNKNOWN -> {
                val errorMessage = "The received QR content cannot be processed"
                log.warn(errorMessage)
                throw NoSuchQrContentException(errorMessage)
            }
        }
    }

    private fun identifyQrContentType(content: String): QrType {

        // define multiple regex patterns to identify the QR content type

        val loginRequestUrlRegex = Regex("(https|http).*?(authentication-request|authentication-requests).*")
        val siopAuthenticationRequestRegex = Regex("openid://.*")
        val credentialOfferUriRegex = Regex("(https|http).*?(credential-offer).*")
        val verifiableCredentialInVcJwtFormatRegex = Regex("ey.*")

        return when {
            loginRequestUrlRegex.matches(content) -> QrType.SIOP_AUTH_REQUEST_URI
            siopAuthenticationRequestRegex.matches(content) -> QrType.SIOP_AUTH_REQUEST
            credentialOfferUriRegex.matches(content) -> QrType.CREDENTIAL_OFFER_URI
            verifiableCredentialInVcJwtFormatRegex.matches(content) -> QrType.VC_JWT
            else -> {
                log.warn("Unknown QR content type: $content")
                QrType.UNKNOWN
            }
        }
    }

}