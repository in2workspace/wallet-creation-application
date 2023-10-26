package es.in2.wallet.controller

import es.in2.wallet.api.controller.TermsOfServiceController
import es.in2.wallet.api.model.dto.TermsOfService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
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
class TermsOfServiceControllerTest {

    @InjectMocks
    private lateinit var termsOfServiceController: TermsOfServiceController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(termsOfServiceController).build()
    }

    @Test
    fun `should return terms of service`() {
        // Arrange
        val expectedTermsOfService = TermsOfService()
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/terms-of-service"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.Content").value(expectedTermsOfService.content))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Disclaimer").value(expectedTermsOfService.disclaimer))
            .andExpect(MockMvcResultMatchers.jsonPath("$.LastUpdated").value(expectedTermsOfService.lastUpdated))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[0]").value(expectedTermsOfService.terms[0]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[1]").value(expectedTermsOfService.terms[1]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[2]").value(expectedTermsOfService.terms[2]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[3]").value(expectedTermsOfService.terms[3]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[4]").value(expectedTermsOfService.terms[4]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[5]").value(expectedTermsOfService.terms[5]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.Terms[6]").value(expectedTermsOfService.terms[6]))
    }

}