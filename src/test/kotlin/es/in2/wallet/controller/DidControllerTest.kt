import es.in2.wallet.wca.controller.DidController
import es.in2.wallet.wca.model.dto.DidRequestDTO
import es.in2.wallet.wca.model.dto.DidResponseDTO
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.service.WalletDidService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(MockitoJUnitRunner::class)
class DidControllerTest {

    @Mock
    private lateinit var orionService: OrionService

    @Mock
    private lateinit var walletDidService: WalletDidService

    @InjectMocks
    private lateinit var didController: DidController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(didController).build()
    }


    @Test
    fun `createDidKey should return 200 OK`() {
        val did = DidRequestDTO("key", null)
        Mockito.`when`(didController.createDid(did)).thenReturn("DID created")

        val jsonRequestDTO = """
            {
              "type": "key",
              "value": null
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dids").contentType(MediaType.APPLICATION_JSON).content(jsonRequestDTO)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

    }


    @Test
    fun `createDidElsi should return 200 OK`() {
        val did = DidRequestDTO("elsi", "did:elsi:56789")
        Mockito.`when`(didController.createDid(did)).thenReturn("DID created")

        val jsonRequestDTO = """
            {
              "type": "elsi",
              "value": "did:elsi:56789"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dids").contentType(MediaType.APPLICATION_JSON).content(jsonRequestDTO)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)


    }

    @Test
    fun `createDid should return 500 INTERNAL SERVER ERROR`() {
        val did = DidRequestDTO("key", "did:web:200")
        val errorMessage = "Value must be null for 'key' type DID"
        Mockito.`when`(didController.createDid(did))

        val jsonRequestDTO = """
            {
              "type": "key",
              "value": "did:web:200"
            }
        """.trimIndent()

        try {
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/dids").contentType(MediaType.APPLICATION_JSON).content(jsonRequestDTO)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.content().string(errorMessage))
        } catch (ex: Exception) {
            println("Error message: ${ex.message}")
            println("Root cause: ${ex.cause?.message}")
        }

    }

    @Test
    fun `createDidElsi should return 500 INTERNAL SERVER ERROR`() {
        val did = DidRequestDTO("elsi", "didweb:123456789abcdef")
        val errorMessage = "DID does not match the pattern"

        Mockito.`when`(didController.createDid(did))

        val jsonRequestDTO = """
            {
              "type": "elsi",
              "value": "didweb:123456789abcdef"
            }
        """.trimIndent()

        try {
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/dids").contentType(MediaType.APPLICATION_JSON).content(jsonRequestDTO)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.content().string(errorMessage))
        } catch (ex: Exception) {
            println("Error message: ${ex.message}")
            println("Root cause: ${ex.cause?.message}")
        }
    }

    @Test
    fun `getDidList should return 200 OK`() {

        val userUUID = "fff36f29-2155-4647-aacf-e01e6f54cc91"

        val responseJsonArray = """
        [
            {
                "did": "did:key:z6MkvP5DbcyqCd8edocU8vU9yEpbnsSopnxCD7bybTPD95gZ"
            },
            {
                "did": "did:elsi:sasas"
            }
        ]
    """.trimIndent()

        val expectedDidResponseDTOs = mutableListOf(
            DidResponseDTO("did:key:z6MkvP5DbcyqCd8edocU8vU9yEpbnsSopnxCD7bybTPD95gZ"),
            DidResponseDTO("did:elsi:sasas")
        )

        Mockito.`when`(didController.getDidList()).thenReturn(expectedDidResponseDTOs)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/dids")
                .param("userId.value", userUUID)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(responseJsonArray))
    }

    @Test
    fun `Delete Did should return 200 OK`() {
        val did = DidResponseDTO("did:key:zDnaeucFNSnCmRGj5VucjxJEJS6yhF9PtnfSjCyBMGza2Wt97")
        val userUUID = "fff36f29-2155-4647-aacf-e01e6f54cc91"
        Mockito.`when`(didController.deleteDid(did)).thenReturn("DID deleted")

        val jsonRequestDTO = """
            {
                "did": "did:key:zDnaeucFNSnCmRGj5VucjxJEJS6yhF9PtnfSjCyBMGza2Wt97"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/dids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestDTO)
                .param("userId.value", userUUID)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)


    }

    @Test
    fun `Delete Did should return 500 INTERNAL SERVER ERROR`() {
        val didResponseDTO = DidResponseDTO("did:key:zDnaeucFNSnCmRGj5VucjxJEJS6yhF9PtnfSjCyBMGza2Wt97")
        val errorMessage = "DID not found: did:key:sdsdsdsdsdsdsdsdsdsdsdsd"
        val userUUID = "fff36f29-2155-4647-aacf-e01e6f54cc91"
        Mockito.`when`(didController.deleteDid(didResponseDTO))

        val jsonRequestDTO = """
            {
                "did": "did:key:sdsdsdsdsdsdsdsdsdsdsdsd"
            }
        """.trimIndent()

        try {
            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/dids")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequestDTO)
                    .param("userId.value", userUUID)
            )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.content().string(errorMessage))
        } catch (ex: Exception) {
            println("Error message: ${ex.message}")
            println("Root cause: ${ex.cause?.message}")
        }


    }
}

