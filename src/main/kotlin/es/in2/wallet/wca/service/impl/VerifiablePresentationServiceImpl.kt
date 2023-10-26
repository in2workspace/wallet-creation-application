package es.in2.wallet.wca.service.impl

import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.VerifiablePresentationService
import es.in2.wallet.api.util.VC_JWT
import es.in2.wallet.wca.service.WalletDidService
import id.walt.credentials.w3c.PresentableCredential
import id.walt.credentials.w3c.VerifiableCredential
import id.walt.custodian.Custodian
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VerifiablePresentationServiceImpl(
    private val walletDidService: WalletDidService,
    private val orionService: OrionService,
    private val siopService: SiopService
) : VerifiablePresentationService {

    private val log: Logger = LogManager.getLogger(VerifiablePresentationServiceImpl::class.java)

    override fun createVerifiablePresentation(vcSelectorResponseDTO: VcSelectorResponseDTO): String {

        // Get vc_jwt list from the selected list of VCs received
        val verifiableCredentialsList = mutableListOf<PresentableCredential>()
        vcSelectorResponseDTO.selectedVcList.forEach {
            verifiableCredentialsList.add(
                PresentableCredential(
                    verifiableCredential = VerifiableCredential.fromString(
                        orionService.getVerifiableCredentialByIdAndFormat(it.id, VC_JWT)),
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
        val holderDid = walletDidService.generateDidKey()

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
