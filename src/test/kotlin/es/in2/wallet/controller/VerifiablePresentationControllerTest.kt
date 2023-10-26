package es.in2.wallet.controller

import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.controller.VerifiablePresentationController
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.VerifiablePresentationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
class VerifiablePresentationControllerTest {

    @Mock
    private lateinit var verifiablePresentationService: VerifiablePresentationService

    @Mock
    private lateinit var siopService: SiopService

    @InjectMocks
    private lateinit var verifiablePresentationController: VerifiablePresentationController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(VerifiablePresentationControllerTest::class.java)
        mockMvc = MockMvcBuilders.standaloneSetup(verifiablePresentationController).build()
    }

    @Test
    fun `createVerifiablePresentation should return 200 OK`() {
        // Create a mock instance of VcSelectorResponseDTO for the request body
        val vcSelectorResponseDTO = VcSelectorResponseDTO(
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            selectedVcList = listOf(
                VcBasicDataDTO(
                    id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                    vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
                    credentialSubject = "default data"
                )
            )
        )

        // Mock the verifiablePresentationService and siopService as needed
        `when`(verifiablePresentationService.createVerifiablePresentation(vcSelectorResponseDTO))
            .thenReturn("vp_jwt")
        `when`(siopService.sendAuthenticationResponse(vcSelectorResponseDTO, "vp_jwt"))
            .thenReturn("access_token")

        // Perform the POST request and assert the response
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/vp")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"redirectUri\":\"http://portal-api:8082/api/verifier/siop-sessions\"," +
                        "\"state\":\"gFfLfeHuTouHjDwoe9vvQw\"," +
                        "\"selectedVcList\":[{" +
                        "\"id\":\"urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba\"," +
                        "\"vcType\":[\"VerifiableCredential\",\"VerifiableAttestation\",\"VerifiableId\"]," +
                        "\"credentialSubject\":\"default data\"}]}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Add additional assertions as needed
    }

}