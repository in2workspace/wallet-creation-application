package es.in2.wallet.controller

import es.in2.wallet.wca.controller.IssuerController
import es.in2.wallet.wca.service.IssuerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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

    private lateinit var issuerController: IssuerController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        issuerController = IssuerController(issuerService)
        mockMvc = MockMvcBuilders.standaloneSetup(issuerController).build()
    }

    @Test
    fun testGetAllIssuers() {
        val issuer1 = "Issuer1"
        val issuer2 = "Issuer2"
        val issuers = listOf(issuer1, issuer2)
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTkyNzU0NjEsImlhdCI6MTY5OTI3NTE2MSwianRpIjoiZjE4YWE4ZjEtMmRlYy00YjA1LThjMDktYjRhYTQ2NjAxNzQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOWY3ZDVhNGUtYTE4ZS00YzRjLThlNTYtYjE0MDBhNmFiNjllIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJhMjA5YWMyMi04MDkzLTQ0NGYtODk1Yy0yZjY1NzZjMjU2ZDkiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiYTIwOWFjMjItODA5My00NDRmLTg5NWMtMmY2NTc2YzI1NmQ5IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqaWFibzAxMiIsImVtYWlsIjoiamlhYm8wMTJAZ21haWwuY29tIn0.3lDZe7QKXajP_2Fflf4IyDHrhVgMfjJN2twCf7g2UUcRRO7tTK0w9kSV_SKI1GF2aptAJ32NB4AZbtcTF8MG9WK5uC8P-wBKYxYFuw_9GCD1ddFN6fCAqNKuRHEJ7kwJ_5PIuSHvp6GfPGNGcGo2u0KTZJyOZ7BNU5iBibNFXQiqSp4hAYz982tvqW3ogM4vq64g32ljnLXdYI4KMLAxB5qxfTKbAd_WkUtGEMCGGACAtN1IDSZaW0FL-q4qSYjcq6NOIf6kIRz9bq0IQ0QFW0f-BrzWw4SN-bAFGzfR8FdZOuU6AtPLX0Y9y730bb4OgwEicAWmcsuVjFs3ML6uVQ"

        `when`(issuerService.getIssuers()).thenReturn(issuers)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/issuers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("Issuer1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("Issuer2"))
    }
}