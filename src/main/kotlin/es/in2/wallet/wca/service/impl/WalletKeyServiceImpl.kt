package es.in2.wallet.wca.service.impl

import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import es.in2.wallet.api.util.SERVICE_MATRIX
import es.in2.wallet.wca.service.WalletKeyService
import id.walt.crypto.KeyAlgorithm
import id.walt.crypto.KeyId
import id.walt.servicematrix.ServiceMatrix
import id.walt.services.key.KeyFormat
import id.walt.services.key.KeyService
import id.walt.services.keystore.KeyType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

@Service
class WalletKeyServiceImpl : WalletKeyService {

    private val log: Logger = LogManager.getLogger(WalletKeyService::class.java)

    override fun generateKey(): KeyId {
        log.info("Key Service - Generate Key")
        ServiceMatrix(SERVICE_MATRIX)
        val keyId = KeyService.getService().generate(KeyAlgorithm.ECDSA_Secp256r1) //KeyAlgorithm.ECDSA_Secp256r1
        log.info("KeyId = {}", keyId)
        return keyId
    }

    override fun getECKeyFromKid(kid: String): ECKey {
        log.info("Key Service - Get ECKey by Kid")
        ServiceMatrix(SERVICE_MATRIX)
        val jwk = KeyService.getService().export(kid, KeyFormat.JWK, KeyType.PRIVATE)
        return JWK.parse(jwk).toECKey()
    }

}
