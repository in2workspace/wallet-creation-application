package es.in2.wallet.controller

import es.in2.wallet.api.controller.QrCodeProcessorController
import es.in2.wallet.api.service.QrCodeProcessorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
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
class QrCodeProcessorControllerTest {

    @Mock
    private lateinit var qrCodeProcessorService: QrCodeProcessorService

    @InjectMocks
    private lateinit var qrCodeProcessorController: QrCodeProcessorController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(QrCodeProcessorControllerTest::class.java)
        mockMvc = MockMvcBuilders.standaloneSetup(qrCodeProcessorController).build()
    }

    @Test
    fun `executeQrContent should return 201 Created`() {
        val qrContent = "qr content"

        `when`(qrCodeProcessorService.processQrContent(anyString())).thenReturn("processed-result")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/execute-content")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"qr_content\":\"$qrContent\"}")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `executeQrContent with mal formatted content should return 400 Bad Request`() {
        val qrContent = "qr content"

        `when`(qrCodeProcessorService.processQrContent(anyString())).thenReturn("processed-result")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/execute-content")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"$qrContent\"}")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

}
