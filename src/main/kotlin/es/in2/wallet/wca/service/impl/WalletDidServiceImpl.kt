package es.in2.wallet.wca.service.impl

import es.in2.wallet.wca.exception.InvalidDidFormatException
import es.in2.wallet.wca.model.entity.DidMethods
import es.in2.wallet.wca.model.dto.DidRequestDTO
import es.in2.wallet.wca.model.dto.DidResponseDTO
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.api.util.SERVICE_MATRIX
import es.in2.wallet.wca.service.WalletDidService
import es.in2.wallet.wca.service.WalletKeyService
import id.walt.model.DidMethod
import id.walt.servicematrix.ServiceMatrix
import id.walt.services.did.DidService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

@Service
class WalletDidServiceImpl(
    private val walletKeyService: WalletKeyService,
    private val orionService: OrionService
) : WalletDidService {

    private val log: Logger = LogManager.getLogger(WalletDidService::class.java)

    override fun createDid(didRequestDTO: DidRequestDTO): String {
        log.debug("DID Service - Create DID")

        val didType = didRequestDTO.type
        val didValue = didRequestDTO.value

        // check if didValue is null for didType = "key"
        checkIfDidKeyValueIsEmpty(didType, didValue)

        return when (didType) {
            "key" -> {
                createDidKey()
            }
            "elsi" -> {
                createDidElsi(didValue.toString())
            }
            else -> throw InvalidDidFormatException("Invalid DID format")
        }
    }

    private fun checkIfDidKeyValueIsEmpty(didType: String, didValue: String?) {
        if (didType == "key" && didValue != null) {
            throw InvalidDidFormatException("Value must be null for 'key' type DID.")
        }
    }

    private fun createDidElsi(elsi: String): String {
        log.debug("DID Service - Create DID ELSI")
        orionService.saveDid(elsi, DidMethods.DID_ELSI)
        log.debug("DID ELSI = {}", elsi)
        return elsi
    }

    private fun createDidKey(): String {
        log.debug("DID Service - Create DID Key")
        val did = generateDidKey()
        log.debug("DID Key = {}", did)
        orionService.saveDid(did, DidMethods.DID_KEY)
        return did
    }

    override fun getDidsByUserId(): List<DidResponseDTO> {
        return orionService.getDidsByUserId()
    }

    override fun deleteDid(didResponseDTO: DidResponseDTO): String{
        val did = didResponseDTO.did
        if(!did.startsWith("did:key:") && !did.startsWith("did:elsi:")){
            throw InvalidDidFormatException("Value DID has an invalid format.")
        }
        orionService.deleteSelectedDid(didResponseDTO)
        return "Deleted " + didResponseDTO.did
    }

    override fun generateDidKey(): String {
        log.info("DID Service - Generate DID Key")
        ServiceMatrix(filePath = SERVICE_MATRIX)
        val keyId = walletKeyService.generateKey().id
        val did = DidService.create(DidMethod.key, keyId)
        log.info("DID Key = {}", did)
        return did
    }

    override fun generateDidKeyWithKid(kid: String): String {
        log.info("DID Service - Generate DID Key by KID")
        ServiceMatrix(filePath = SERVICE_MATRIX)
        return DidService.create(DidMethod.key, kid)
    }

}