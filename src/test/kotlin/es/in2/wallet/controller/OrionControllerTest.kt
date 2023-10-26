
package es.in2.wallet.controller

import es.in2.wallet.integration.orion.controller.OrionController
import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.integration.orion.service.OrionService
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
class OrionControllerTest {

    @Mock
    private lateinit var orionService: OrionService

    @InjectMocks
    private lateinit var orionController: OrionController

    private lateinit var mockMvc: MockMvc



    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(OrionControllerTest::class.java)
        mockMvc = MockMvcBuilders.standaloneSetup(orionController).build()
    }

    @Test
    fun `getVerifiableCredentialList should return 200 OK`() {
        // Create a list of VcBasicDataDTO objects for the expected response
        val vcBasicDataDTO = VcBasicDataDTO(
            id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
            vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
            credentialSubject = "subject data"
        )

        // Add objects to the list as needed
        val expectedList = mutableListOf(vcBasicDataDTO)


        // Mock the personalDataSpaceService to return the expected list
        `when`(orionService.getUserVCsInJson()).thenReturn(expectedList)

        // Perform the GET request and assert the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/personal-data-space"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].vcType").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].vcType", Matchers.hasSize<String>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].vcType[0]").value("VerifiableCredential"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].vcType[1]").value("VerifiableAttestation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].vcType[2]").value("VerifiableId"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].credentialSubject").value("subject data"))

        // Add additional assertions as needed
    }

}


