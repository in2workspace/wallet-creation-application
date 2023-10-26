package es.in2.wallet.domain.dtos

import es.in2.wallet.api.model.dto.AppUserResponseDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class AppUserResponseDTOTest {

    @Test
    fun testDTOProperties() {
        // Create test data
        val uuid = UUID.randomUUID().toString()
        val username = "johnDoe"
        val email = "john@example.com"
        // Create an instance of the DTO
        val (uuid1, username1, email1) = AppUserResponseDTO(uuid, username, email)
        // Verify the properties
        Assertions.assertEquals(uuid, uuid1)
        Assertions.assertEquals(username, username1)
        Assertions.assertEquals(email, email1)
    }

}