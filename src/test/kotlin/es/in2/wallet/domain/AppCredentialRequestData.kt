package es.in2.wallet.domain

import es.in2.wallet.wca.model.entity.CredentialRequestData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class CredentialRequestDataTest {

    @Test
    fun testAppCredentialRequestDataProperties() {
        // Create test data
        val id = UUID.randomUUID()
        val issuerName = "issuer123"
        val userId = UUID.randomUUID().toString()
        val issuerNonce = "nonce123"
        val issuerAccessToken = "accessToken123"

        // Create an instance of the AppIssuerData entity
        val credentialRequestData = CredentialRequestData(
                id = id,
                issuerName = issuerName,
                userId = userId,
                issuerNonce = issuerNonce,
                issuerAccessToken = issuerAccessToken
        )

        // Verify the properties
        Assertions.assertEquals(id, credentialRequestData.id)
        Assertions.assertEquals(issuerName, credentialRequestData.issuerName)
        Assertions.assertEquals(userId, credentialRequestData.userId)
        Assertions.assertEquals(issuerNonce, credentialRequestData.issuerNonce)
        Assertions.assertEquals(issuerAccessToken, credentialRequestData.issuerAccessToken)
    }
}