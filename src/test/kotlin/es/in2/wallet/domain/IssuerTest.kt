package es.in2.wallet.domain

import es.in2.wallet.api.model.entity.Issuer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class IssuerTest {

    @Test
    fun testAppIssuerDataProperties() {
        // Create test data
        val id: UUID = UUID.randomUUID()
        val issuerName = "issuer123"
        val issuerMetadata = """{"key": "value"}"""

        // Create an instance of the AppIssuerData entity
        val issuer = Issuer(
            id = id,
            name = issuerName,
            metadata = issuerMetadata
        )

        // Verify the properties
        Assertions.assertEquals(id, issuer.id)
        Assertions.assertEquals(issuerName, issuer.name)
        Assertions.assertEquals(issuerMetadata, issuer.metadata)

    }
}