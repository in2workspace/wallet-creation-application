package es.in2.wallet.domain.dtos

import es.in2.wallet.api.model.dto.QrContentDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class QrContentDTOTest {

    @Test
    fun testDTOProperties() {
        // Create test data
        val content = "example content"
        // Create an instance of the DTO
        val (content1) = QrContentDTO(content)
        // Verify the property
        Assertions.assertEquals(content, content1)
    }

}
