package es.in2.wallet.domain

import es.in2.wallet.api.model.entity.AppUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class AppUserTest {

    @Test
    fun testAppUserProperties() {
        // Create test data
        val id: UUID = UUID.randomUUID()
        val username = "johnDoe"
        val email = "john@example.com"
        val password = "secretPassword"
        // Create an instance of the AppUser entity
        val (id1, username1, email1, password1) = AppUser(id, username, email, password)
        // Verify the properties
        Assertions.assertEquals(id, id1)
        Assertions.assertEquals(username, username1)
        Assertions.assertEquals(email, email1)
        Assertions.assertEquals(password, password1)
    }

}