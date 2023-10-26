package es.in2.wallet.controller

import es.in2.wallet.api.controller.IssuerController
import es.in2.wallet.api.service.IssuerService
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
class IssuerControllerTest {

    @Mock
    private lateinit var issuerService: IssuerService

    @InjectMocks
    private lateinit var issuerController: IssuerController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(AppUserControllerTest::class.java)
        mockMvc = MockMvcBuilders.standaloneSetup(issuerController).build()
    }

    @Test
    fun testGetAllIssuers() {
        val issuer1 = "Issuer1"
        val issuer2 = "Issuer2"
        val issuers = listOf(issuer1, issuer2)

        `when`(issuerService.getIssuers()).thenReturn(issuers)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/issuers"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("Issuer1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("Issuer2"))
    }


}