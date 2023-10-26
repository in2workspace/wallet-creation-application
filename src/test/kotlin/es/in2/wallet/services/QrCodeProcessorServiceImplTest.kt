package es.in2.wallet.services

import es.in2.wallet.api.controller.QrCodeProcessorController
import es.in2.wallet.api.exception.NoSuchQrContentException
import es.in2.wallet.integration.orion.service.impl.OrionServiceImpl
import es.in2.wallet.api.service.impl.QrCodeProcessorServiceImpl
import es.in2.wallet.wca.service.impl.SiopServiceImpl
import es.in2.wallet.wca.service.impl.VerifiableCredentialServiceImpl
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
class QrCodeProcessorServiceImplTest {

    @MockBean
    private lateinit var siopService: SiopServiceImpl

    @MockBean
    private lateinit var verifiableCredentialService: VerifiableCredentialServiceImpl

    @MockBean
    private lateinit var personalDataSpaceService: OrionServiceImpl

    @MockBean
    private lateinit var qrCodeProcessorService: QrCodeProcessorServiceImpl

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(QrCodeProcessorController(qrCodeProcessorService)).build()
        qrCodeProcessorService = QrCodeProcessorServiceImpl(
            siopService,
            verifiableCredentialService,
            personalDataSpaceService
        )
    }

    @Test
    fun testProcessQrContentCredentialOfferUri() {
        // Mock behavior
        val qrContent = "https://www.goodair.com/credential-offer?credential_offer_uri=https://www.goodair.com/credential-offer/5j349k3e3n23j"
        // Call the method
        qrCodeProcessorService.processQrContent(qrContent)
        // Verify behavior and assertions
        verify(verifiableCredentialService).getCredentialIssuerMetadata(qrContent)
    }

    @Test
    @EnabledIf(expression = "#{environment['spring.profiles.active'] == 'local'}", loadContext = true)
    fun testProcessQrContentVcJwt() {
        // Mock behavior
        val qrContent =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9" +
                    "lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        // Verify behavior and assertions
        verify(personalDataSpaceService).saveVC(qrContent)
    }

    @Test
    fun testProcessQrContentUnknown() {
        // Call the method with unknown QR content
        val qrContent = "unknown-content"
        // Call the method
        val exception = assertThrows(NoSuchQrContentException::class.java) {
            qrCodeProcessorService.processQrContent(qrContent)
        }
        // Verify behavior and assertions
        Assertions.assertEquals("The received QR content cannot be processed", exception.message)
    }

    @Test
    fun `processQrContent should throw NoSuchQrContentException when QR content is unknown`() {
        val qrContent = "unknown-content"
        mockMvc.perform(get("/process-qr-content?qrContent=$qrContent"))
            .andExpect(status().isNotFound)
    }

}