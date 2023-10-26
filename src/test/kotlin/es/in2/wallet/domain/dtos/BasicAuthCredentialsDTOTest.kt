package es.in2.wallet.domain.dtos

import es.in2.wallet.api.model.dto.BasicAuthCredentialsDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BasicAuthCredentialsDTOTest {

    @Test
    fun testDTOProperties() {
        // Create test data
        val username = "johnDoe"
        val password = "secretpassword"
        // Create an instance of the DTO
        val (username1, password1) = BasicAuthCredentialsDTO(username, password)
        // Verify the properties
        Assertions.assertEquals(username, username1)
        Assertions.assertEquals(password, password1)
    }

}