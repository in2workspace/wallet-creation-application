package es.in2.wallet.wca.service.impl

import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.service.VerifiablePresentationService
import es.in2.wallet.wca.util.*
import id.walt.credentials.w3c.PresentableCredential
import id.walt.credentials.w3c.VerifiableCredential
import id.walt.crypto.KeyAlgorithm
import id.walt.custodian.Custodian
import id.walt.model.DidMethod
import id.walt.servicematrix.ServiceMatrix
import id.walt.services.did.DidService
import id.walt.services.key.KeyFormat
import id.walt.services.key.KeyService
import id.walt.services.keystore.KeyType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VerifiablePresentationServiceImpl(
    @Value("\${app.url.wallet-data-baseurl}") private val walletDataBaseUrl: String,
    @Value("\${app.url.wallet-crypto-baseurl}") private val walletCryptoBaseUrl: String,
    private val applicationUtils: ApplicationUtils
) : VerifiablePresentationService {

    private val log: Logger = LogManager.getLogger(VerifiablePresentationServiceImpl::class.java)

    override fun createVerifiablePresentation(vcSelectorResponseDTO: VcSelectorResponseDTO, token: String): String {

        // Get vc_jwt list from the selected list of VCs received
        val verifiableCredentialsList = mutableListOf<PresentableCredential>()
        vcSelectorResponseDTO.selectedVcList.forEach {
            val url = walletDataBaseUrl + GET_VC_BY_ID_FORMAT + "?credentialId=" + it.id + "&format=vc_jwt"
            val headers = listOf(
                CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON,
                HEADER_AUTHORIZATION to "Bearer $token"
            )
            val response = applicationUtils.getRequest(url = url, headers = headers)
            log.info("verifiable credential by Id and Format = {}", response)
            verifiableCredentialsList.add(
                PresentableCredential(
                    verifiableCredential = VerifiableCredential.fromString(response),
                    discloseAll = false
            ))
        }

        // Get Subject DID
        val subjectDid = getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList)
        log.info("subject DID = {}", subjectDid)

        /*
            The holder DID MUST be received by the Wallet implementation, and it MUST match with the
            subject_id of, at least, one of the VCs attached.
            That VP MUST be signed using the PrivateKey related with the holderDID.
         */

/*
        val url = walletCryptoBaseUrl + GET_DID_KEY
        val headers = listOf(
            CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON,
            HEADER_AUTHORIZATION to "Bearer $token"
        )
        val holderDid = applicationUtils.postRequest(url = url, headers = headers, body = "")
*/

        log.info("Key Service - Generate Key")
        ServiceMatrix(SERVICE_MATRIX)
        val keyId = KeyService.getService().generate(KeyAlgorithm.ECDSA_Secp256r1)
        log.info("KeyId = {}", keyId)
        val holderDid = DidService.create(DidMethod.key, keyId.id)
        /*
            The holder SHOULD be able to modify the attribute 'expiration_date' by any of its
            Verifiable Presentation.
        */
        val secondsToAdd: Long = 60000

        return Custodian.getService().createPresentation(
            vcs = verifiableCredentialsList,
            holderDid = holderDid,
            expirationDate = Instant.now().plusSeconds(secondsToAdd)
        )
    }

    private fun getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList: MutableList<PresentableCredential>): String {
        val verifiableCredential = verifiableCredentialsList[0].verifiableCredential.toString()
        val parsedVerifiableCredential = SignedJWT.parse(verifiableCredential)
        val payloadToJson = parsedVerifiableCredential.payload.toJSONObject()
        return payloadToJson["sub"].toString()
    }

}
