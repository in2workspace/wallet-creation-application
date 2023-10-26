package es.in2.wallet.api.security

import es.in2.wallet.api.util.SERVICE_MATRIX
import id.walt.crypto.KeyAlgorithm
import id.walt.model.DidMethod
import id.walt.servicematrix.ServiceMatrix
import id.walt.services.did.DidService
import id.walt.services.key.KeyService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class WalletDidKeyConfig {

    private val log: Logger = LoggerFactory.getLogger(WalletDidKeyConfig::class.java)

    @Bean
    @Profile("default")
    fun didKeyGeneratorTest(): WalletDidKeyGenerator {
        return WalletDidKeyGenerator("")
    }

    @Bean
    @Profile("!default")
    fun didKeyGenerator(): WalletDidKeyGenerator {
        ServiceMatrix(SERVICE_MATRIX)
        val keyId = KeyService.getService().generate(KeyAlgorithm.ECDSA_Secp256r1)
        val didKey = DidService.create(DidMethod.key, keyId.id)
        return WalletDidKeyGenerator(didKey)
    }

}
