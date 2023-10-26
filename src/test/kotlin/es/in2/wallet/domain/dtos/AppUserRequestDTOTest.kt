package es.in2.wallet.domain.dtos

import es.in2.wallet.api.model.dto.AppUserRequestDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AppUserRequestDTOTest {

    @Test
    fun testDTOProperties() {
        // Create test data
        val username = "johnDoe"
        val email = "john@example.com"
        val password = "secretpassword"
        // Create an instance of the DTO
        val (username1, email1, password1) = AppUserRequestDTO(username, email, password)
        // Verify the properties
        Assertions.assertEquals(username, username1)
        Assertions.assertEquals(email, email1)
        Assertions.assertEquals(password, password1)
    }

}
