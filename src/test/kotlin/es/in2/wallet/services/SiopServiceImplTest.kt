package es.in2.wallet.services

import es.in2.wallet.wca.model.dto.OpenIdConfig
import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.TokenVerificationService
import es.in2.wallet.wca.service.impl.SiopServiceImpl
import es.in2.wallet.wca.util.ApplicationUtils
import es.in2.wallet.wca.util.JWT_VC
import es.in2.wallet.wca.util.JWT_VP
import id.walt.model.dif.DescriptorMapping
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SiopServiceImplTest {

    private var tokenVerificationService: TokenVerificationService = mockk()

    private val applicationUtils: ApplicationUtils = mockk(relaxed = true)

    private lateinit var siopServiceImplTest: SiopServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        siopServiceImplTest = SiopServiceImpl("walletDataBaseUrl", tokenVerificationService, applicationUtils)
    }

    @Test
    fun testGetSiopAuthenticationRequest() {
        val jwtSiopAuthRequest = "eyJraWQiOiJkaWQ6a2V5OnpEbmFld0F5NDJOUmQ5QmVzNjgza2pYYWVtY1lqa2pDTGEzeGNyWWQxU3B0TXZyYWgiLCJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJhdXRoX3JlcXVlc3QiOiJvcGVuaWQ6Ly8_c2NvcGU9W0xFQVJDcmVkZW50aWFsXSZyZXNwb25zZV90eXBlPXZwX3Rva2VuJnJlc3BvbnNlX21vZGU9ZGlyZWN0X3Bvc3QmY2xpZW50X2lkPWRpZDprZXk6ekRuYWV3QXk0Mk5SZDlCZXM2ODNralhhZW1jWWprakNMYTN4Y3JZZDFTcHRNdnJhaCZzdGF0ZT1PaUUycmZGdlNwNmg3Y0Raem9kbk1nJm5vbmNlPWUwMDgwN2I4LWI4ZGUtNDE1ZC1iOWM2LTI2YmZlOTBkODU1ZCZyZWRpcmVjdF91cmk9aHR0cDovL3BvcnRhbC1hcGk6ODA4Ny9hcGkvdmVyaWZpZXIvc2lvcC1zZXNzaW9ucyIsInN1YiI6ImRpZDprZXk6ekRuYWV3QXk0Mk5SZDlCZXM2ODNralhhZW1jWWprakNMYTN4Y3JZZDFTcHRNdnJhaCIsImF1ZCI6Imh0dHBzOi8vc2VsZi1pc3N1ZWQubWUvdjIiLCJpc3MiOiJkaWQ6a2V5OnpEbmFld0F5NDJOUmQ5QmVzNjgza2pYYWVtY1lqa2pDTGEzeGNyWWQxU3B0TXZyYWgiLCJleHAiOjE2OTkzNjM2NDgsImlhdCI6MTY5OTM2MzA0OH0.nSTS1e-8Gwa_KCy3nMa2k8AXIjo5unybOHQHfemIN72PHMox_JS7BdY2vRl-lIe94lg6hNFxGp7ZtZIB_95GmQ"

        every {
            applicationUtils.getRequest(any(), any())
        } returns jwtSiopAuthRequest

        justRun {
            tokenVerificationService.verifySiopAuthRequestAsJwsFormat(any())
        }

        val openIdConfig = OpenIdConfig(
            scope = "scope",
            responseType = "responseType",
            responseMode = "responseMode",
            clientId = "clientId",
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            nonce = "nonce"
        )

        every {
            applicationUtils.parseOpenIdConfig(any())
        } returns openIdConfig

        val mockedResponse = "[{\"id\":\"urn:uuid:dd0a698f-7477-41cb-b4c5-4322c864649b\",\"vcType\":[\"VerifiableCredential\",\"LEARCredential\"],\"credentialSubject\":{\"id\":\"did:key:zQ3shqzDDz8d77vqMGqaci2Y3qNpnY5hbuVVsrVUtP5gakfhk\",\"title\":\"Mr.\",\"first_name\":\"John\",\"last_name\":\"Doe\",\"gender\":\"M\",\"postal_address\":\"\",\"email\":\"johndoe@goodair.com\",\"telephone\":\"\",\"fax\":\"\",\"mobile_phone\":\"+34787426623\",\"legalRepresentative\":{\"cn\":\"56565656V Jesus Ruiz\",\"serialNumber\":\"56565656V\",\"organizationIdentifier\":\"VATES-12345678\",\"o\":\"GoodAir\",\"c\":\"ES\"},\"rolesAndDuties\":[{\"type\":\"LEARCredential\",\"id\":\"https://dome-marketplace.eu//lear/v1/6484994n4r9e990494\"}],\"key\":\"value\"}}]\n"

        every {
            applicationUtils.postRequest(any(), any(), any())
        } returns mockedResponse

        val siopAuthenticationRequestUri = "http://localhost:8087/api/verifier/authentication-requests?state=FYHCoK6JR96zK3YfPCX-zg"

        val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTkzNTM3NDMsImlhdCI6MTY5OTM1MzQ0MywianRpIjoiNDk1NjMyMTktM2UxNy00YmE0LThmOTctMzRlZGVhM2ZkNjZiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzVjYWRlY2MtZTRkZS00NGEyLWJkODctMDZjNjdjZTkxMTFiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiIyMGEwYjNlMy05MTkzLTQ2ODctOWNhOS0wMGU2ODIyNjdkYmQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiMjBhMGIzZTMtOTE5My00Njg3LTljYTktMDBlNjgyMjY3ZGJkIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqaWFibzAxMjMiLCJlbWFpbCI6ImppYWJvMDEyM0BnbWFpbC5jb20ifQ.QZ6YTj2gBcrPuqwoupU5v0fVUNij-oWQqkLxvs2dzbZIhAX00j-lISXaa82NXIqm9_8HYGsFLMtk6XcSz9_V7IOlEMUnVfCiYciCFjYtJEgETUu-CPq81pfTeVeutC53ocX5HZJETDnbv7ctlNHzKL2nxH4VsRDC1t6v7pDPCVfRKMLd_07Lq_lPyxvcjdv0SXXH9TPq0xFEts8yCEeg-9rKeDTy-FrgO74FXfoteKgFh3V-frHRnoOLccsHg6dnXuG_xltlCGxUE6gcqNsi0nhLzvgjKbeffGJcGyOKyw6pi_GBTJ9pYj-eQ0mue-DBGhRBRK9CEB3jrz5TkKpXPw"

        // Call the method to be tested
        val vcSelectorRequestDTOResponse = siopServiceImplTest.getSiopAuthenticationRequest(siopAuthenticationRequestUri, token)

        val expectedVcSelectorRequestDTO = VcSelectorRequestDTO(
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            selectableVcList = listOf(
                VcBasicDataDTO(
                    id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                    vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
                    credentialSubject = "default data"
                )
            )
        )

        Assertions.assertEquals(vcSelectorRequestDTOResponse.redirectUri, expectedVcSelectorRequestDTO.redirectUri)
        Assertions.assertEquals(vcSelectorRequestDTOResponse.state, expectedVcSelectorRequestDTO.state)
    }

    @Test
    fun testProcessSiopAuthenticationRequest() {
        val openIdConfig = OpenIdConfig(
            scope = "scope",
            responseType = "responseType",
            responseMode = "responseMode",
            clientId = "clientId",
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            nonce = "nonce"
        )

        every {
            applicationUtils.parseOpenIdConfig(any())
        } returns openIdConfig

        val mockedResponse =
            "[{\"id\":\"urn:uuid:dd0a698f-7477-41cb-b4c5-4322c864649b\",\"vcType\":[\"VerifiableCredential\",\"LEARCredential\"],\"credentialSubject\":{\"id\":\"did:key:zQ3shqzDDz8d77vqMGqaci2Y3qNpnY5hbuVVsrVUtP5gakfhk\",\"title\":\"Mr.\",\"first_name\":\"John\",\"last_name\":\"Doe\",\"gender\":\"M\",\"postal_address\":\"\",\"email\":\"johndoe@goodair.com\",\"telephone\":\"\",\"fax\":\"\",\"mobile_phone\":\"+34787426623\",\"legalRepresentative\":{\"cn\":\"56565656V Jesus Ruiz\",\"serialNumber\":\"56565656V\",\"organizationIdentifier\":\"VATES-12345678\",\"o\":\"GoodAir\",\"c\":\"ES\"},\"rolesAndDuties\":[{\"type\":\"LEARCredential\",\"id\":\"https://dome-marketplace.eu//lear/v1/6484994n4r9e990494\"}],\"key\":\"value\"}}]\n"

        every {
            applicationUtils.postRequest(any(), any(), any())
        } returns mockedResponse

        val siopAuthenticationRequest =
            "openid://?scope=[LEARCredential]&response_type=vp_token&response_mode=direct_post&client_id=did:key:zDnaewAy42NRd9Bes683kjXaemcYjkjCLa3xcrYd1SptMvrah&state=pULBqmqFTQGPTziY8NUV5A&nonce=5cc86225-a353-4f47-801b-4aae30967a00&redirect_uri=http://portal-api:8087/api/verifier/siop-sessions"
        val token =
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTkzNTM3NDMsImlhdCI6MTY5OTM1MzQ0MywianRpIjoiNDk1NjMyMTktM2UxNy00YmE0LThmOTctMzRlZGVhM2ZkNjZiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzVjYWRlY2MtZTRkZS00NGEyLWJkODctMDZjNjdjZTkxMTFiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiIyMGEwYjNlMy05MTkzLTQ2ODctOWNhOS0wMGU2ODIyNjdkYmQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiMjBhMGIzZTMtOTE5My00Njg3LTljYTktMDBlNjgyMjY3ZGJkIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqaWFibzAxMjMiLCJlbWFpbCI6ImppYWJvMDEyM0BnbWFpbC5jb20ifQ.QZ6YTj2gBcrPuqwoupU5v0fVUNij-oWQqkLxvs2dzbZIhAX00j-lISXaa82NXIqm9_8HYGsFLMtk6XcSz9_V7IOlEMUnVfCiYciCFjYtJEgETUu-CPq81pfTeVeutC53ocX5HZJETDnbv7ctlNHzKL2nxH4VsRDC1t6v7pDPCVfRKMLd_07Lq_lPyxvcjdv0SXXH9TPq0xFEts8yCEeg-9rKeDTy-FrgO74FXfoteKgFh3V-frHRnoOLccsHg6dnXuG_xltlCGxUE6gcqNsi0nhLzvgjKbeffGJcGyOKyw6pi_GBTJ9pYj-eQ0mue-DBGhRBRK9CEB3jrz5TkKpXPw"

        val vcSelectorRequestDTOResponse =
            siopServiceImplTest.processSiopAuthenticationRequest(siopAuthenticationRequest, token)

        val expectedVcSelectorRequestDTO = VcSelectorRequestDTO(
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            selectableVcList = listOf(
                VcBasicDataDTO(
                    id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                    vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
                    credentialSubject = "default data"
                )
            )
        )

        Assertions.assertEquals(vcSelectorRequestDTOResponse.redirectUri, expectedVcSelectorRequestDTO.redirectUri)
        Assertions.assertEquals(vcSelectorRequestDTOResponse.state, expectedVcSelectorRequestDTO.state)
    }

    @Test
    fun testSendAuthenticationResponse() {
        every {
            applicationUtils.postRequest(any(), any(), any())
        } returns "mockedResponse"

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

        val vp ="eyJraWQiOiJkaWQ6a2V5OnpEbmFlam5kaWdMY0hQQXFBVmRRQUdIQVVlS2FWUUtaNllYVndvbkZxOUZDeERpRGUjekRuYWVqbmRpZ0xjSFBBcUFWZFFBR0hBVWVLYVZRS1o2WVhWd29uRnE5RkN4RGlEZSIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2In0.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlam5kaWdMY0hQQXFBVmRRQUdIQVVlS2FWUUtaNllYVndvbkZxOUZDeERpRGUiLCJuYmYiOjE2OTk1MTU3MDgsImlzcyI6ImRpZDprZXk6ekRuYWVqbmRpZ0xjSFBBcUFWZFFBR0hBVWVLYVZRS1o2WVhWd29uRnE5RkN4RGlEZSIsInZwIjp7InR5cGUiOlsiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiJdLCJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJpZCI6InVybjp1dWlkOmE2ODFkNDYxLTliYzUtNDc4Mi04MWY1LTQzN2I3NzgxZGQ4YSIsImhvbGRlciI6ImRpZDprZXk6ekRuYWVqbmRpZ0xjSFBBcUFWZFFBR0hBVWVLYVZRS1o2WVhWd29uRnE5RkN4RGlEZSIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbImV5SnJhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9VR28yVEhsT1pVMWllbXQzY1RKRmMzQkhjR0pPTmxOM1EyZHZZVEZtZUZCV05rVkhlSGRwZERKdGIxb2plbEV6YzJoUWFqWk1lVTVsVFdKNmEzZHhNa1Z6Y0Vkd1lrNDJVM2REWjI5aE1XWjRVRlkyUlVkNGQybDBNbTF2V2lJc0luUjVjQ0k2SWtwWFZDSXNJbUZzWnlJNklrVlRNalUyU3lKOS5leUp6ZFdJaU9pSmthV1E2YTJWNU9ucEVibUZsVm1WaVdEaDBZbVIxV0hJMlMyVlllRXgxYmtOdmF6RjFjRzlaTTI1bFVqTnVaV2h3WlZJeFlYVnFiakVpTENKdVltWWlPakUyT1RrME5ETTBPVE1zSW1semN5STZJbVJwWkRwclpYazZlbEV6YzJoUWFqWk1lVTVsVFdKNmEzZHhNa1Z6Y0Vkd1lrNDJVM2REWjI5aE1XWjRVRlkyUlVkNGQybDBNbTF2V2lJc0ltVjRjQ0k2TVRjd01qQXpOVFE1TXl3aWFXRjBJam94TmprNU5EUXpORGt6TENKMll5STZleUowZVhCbElqcGJJbFpsY21sbWFXRmliR1ZEY21Wa1pXNTBhV0ZzSWl3aVRFVkJVa055WldSbGJuUnBZV3dpWFN3aVFHTnZiblJsZUhRaU9sc2lhSFIwY0hNNkx5OTNkM2N1ZHpNdWIzSm5Mekl3TVRndlkzSmxaR1Z1ZEdsaGJITXZkakVpTENKb2RIUndjem92TDJSdmJXVXRiV0Z5YTJWMGNHeGhZMlV1WlhVdkx6SXdNakl2WTNKbFpHVnVkR2xoYkhNdmJHVmhjbU55WldSbGJuUnBZV3d2ZGpFaVhTd2lhV1FpT2lKMWNtNDZkWFZwWkRwaU1tSTRNbUl3WWkwNU5XWXpMVFExT1dFdFlXWmtPQzFrWlRJMVpXSmhZelkzTWpZaUxDSnBjM04xWlhJaU9uc2lhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9VR28yVEhsT1pVMWllbXQzY1RKRmMzQkhjR0pPTmxOM1EyZHZZVEZtZUZCV05rVkhlSGRwZERKdGIxb2lmU3dpYVhOemRXRnVZMlZFWVhSbElqb2lNakF5TXkweE1TMHdPRlF4TVRvek9Eb3hNMW9pTENKcGMzTjFaV1FpT2lJeU1ESXpMVEV4TFRBNFZERXhPak00T2pFeldpSXNJblpoYkdsa1JuSnZiU0k2SWpJd01qTXRNVEV0TURoVU1URTZNemc2TVROYUlpd2laWGh3YVhKaGRHbHZia1JoZEdVaU9pSXlNREl6TFRFeUxUQTRWREV4T2pNNE9qRXpXaUlzSW1OeVpXUmxiblJwWVd4VGRXSnFaV04wSWpwN0ltbGtJam9pWkdsa09tdGxlVHA2Ukc1aFpWWmxZbGc0ZEdKa2RWaHlOa3RsV0hoTWRXNURiMnN4ZFhCdldUTnVaVkl6Ym1Wb2NHVlNNV0YxYW00eElpd2lkR2wwYkdVaU9pSk5jaTRpTENKbWFYSnpkRjl1WVcxbElqb2lTbTlvYmlJc0lteGhjM1JmYm1GdFpTSTZJa1J2WlNJc0ltZGxibVJsY2lJNklrMGlMQ0p3YjNOMFlXeGZZV1JrY21WemN5STZJaUlzSW1WdFlXbHNJam9pYW05b2JtUnZaVUJuYjI5a1lXbHlMbU52YlNJc0luUmxiR1Z3YUc5dVpTSTZJaUlzSW1aaGVDSTZJaUlzSW0xdlltbHNaVjl3YUc5dVpTSTZJaXN6TkRjNE56UXlOall5TXlJc0lteGxaMkZzVW1Wd2NtVnpaVzUwWVhScGRtVWlPbnNpWTI0aU9pSTFOalUyTlRZMU5sWWdTbVZ6ZFhNZ1VuVnBlaUlzSW5ObGNtbGhiRTUxYldKbGNpSTZJalUyTlRZMU5qVTJWaUlzSW05eVoyRnVhWHBoZEdsdmJrbGtaVzUwYVdacFpYSWlPaUpXUVZSRlV5MHhNak0wTlRZM09DSXNJbThpT2lKSGIyOWtRV2x5SWl3aVl5STZJa1ZUSW4wc0luSnZiR1Z6UVc1a1JIVjBhV1Z6SWpwYmV5SjBlWEJsSWpvaVRFVkJVa055WldSbGJuUnBZV3dpTENKcFpDSTZJbWgwZEhCek9pOHZaRzl0WlMxdFlYSnJaWFJ3YkdGalpTNWxkUzh2YkdWaGNpOTJNUzgyTkRnME9UazBialJ5T1dVNU9UQTBPVFFpZlYwc0ltdGxlU0k2SW5aaGJIVmxJbjE5TENKcWRHa2lPaUoxY200NmRYVnBaRHBpTW1JNE1tSXdZaTA1TldZekxUUTFPV0V0WVdaa09DMWtaVEkxWldKaFl6WTNNallpZlEuenlZeEg1bGZjZTJPSGlwbDBWMUI3QkJaaGRoMkdiQTdzcGd3VlFvUm8xaThwRnVVUDYxLWlHX1ZieXB0QWQ2M3FrZHFPSEhiT2loNmxqdmVRN2xEOWd-Il19LCJleHAiOjE2OTk1NzU3MDgsImlhdCI6MTY5OTUxNTcwOCwianRpIjoidXJuOnV1aWQ6YTY4MWQ0NjEtOWJjNS00NzgyLTgxZjUtNDM3Yjc3ODFkZDhhIn0.8T6yG3GZwPOcv2A1RCZoYhyU3LvjhFqTKP0C7DRCrzZjADJiNykdwsqGDdMBSaNXvhoQWyFzew1PoY0_EeLa2g"

        val response = siopServiceImplTest.sendAuthenticationResponse(vcSelectorResponseDTO, vp)

        Assertions.assertEquals(response, "mockedResponse")

    }

    @Test
    fun testGenerateDescriptorMap(){
        val vpToken = "eyJraWQiOiJkaWQ6a2V5OnpEbmFlVjFyUjdHSzN3Z2Q5c2lRYVFtWWtQa3gzM0VxUHFqTFU2QlpWdnZYOENnaHAjekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2In0.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlVjFyUjdHSzN3Z2Q5c2lRYVFtWWtQa3gzM0VxUHFqTFU2QlpWdnZYOENnaHAiLCJuYmYiOjE2ODcyNDUzMjUsImlzcyI6ImRpZDprZXk6ekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInZwIjp7InR5cGUiOlsiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiJdLCJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJpZCI6InVybjp1dWlkOjk0YTcyZmQ1LWY2ZGItNDc0Yy04OWQ3LTg3M2Q5YzkxMzdhYSIsImhvbGRlciI6ImRpZDprZXk6ekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbImV5SnJhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9jalUyV2xkTFFtZzJNWFY0V2pSVVMwZDJPRE5TVUhsTU9IVjJObmhqUjNSVFVrcFJWbU4zUVhKaGFUUWplbEV6YzJoeU5UWmFWMHRDYURZeGRYaGFORlJMUjNZNE0xSlFlVXc0ZFhZMmVHTkhkRk5TU2xGV1kzZEJjbUZwTkNJc0luUjVjQ0k2SWtwWFZDSXNJbUZzWnlJNklrVlRNalUyU3lKOS5leUp6ZFdJaU9pSmthV1E2YTJWNU9ucFJNM05vVjJKNGFGaHJjVkpHU0ZORFZEUjFRMmx5UlU1MU5uaE1ka2hqWldaSU1UUm9XWHBTUW5sQmQwTkdOR1lpTENKdVltWWlPakUyT0RVMk1URTJOalVzSW1semN5STZJbVJwWkRwclpYazZlbEV6YzJoeU5UWmFWMHRDYURZeGRYaGFORlJMUjNZNE0xSlFlVXc0ZFhZMmVHTkhkRk5TU2xGV1kzZEJjbUZwTkNJc0ltVjRjQ0k2TVRZNE9ESXdNelkyTlN3aWFXRjBJam94TmpnMU5qRXhOalkxTENKMll5STZleUowZVhCbElqcGJJbFpsY21sbWFXRmliR1ZEY21Wa1pXNTBhV0ZzSWl3aVZtVnlhV1pwWVdKc1pVRjBkR1Z6ZEdGMGFXOXVJaXdpVm1WeWFXWnBZV0pzWlVsa0lsMHNJa0JqYjI1MFpYaDBJanBiSW1oMGRIQnpPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1ERTRMMk55WldSbGJuUnBZV3h6TDNZeElsMHNJbWxrSWpvaWRYSnVPblYxYVdRNk5UbGtZV0psWXpBdFlUWm1NUzAwTkRVMUxUaG1NMll0WXpFek9UVTFaakkzWW1KaElpd2lhWE56ZFdWeUlqb2laR2xrT210bGVUcDZVVE56YUhJMU5scFhTMEpvTmpGMWVGbzBWRXRIZGpnelVsQjVURGgxZGpaNFkwZDBVMUpLVVZaamQwRnlZV2swSWl3aWFYTnpkV0Z1WTJWRVlYUmxJam9pTWpBeU15MHdOaTB3TVZRd09Ub3lOem8wTlZvaUxDSnBjM04xWldRaU9pSXlNREl6TFRBMkxUQXhWREE1T2pJM09qUTFXaUlzSW5aaGJHbGtSbkp2YlNJNklqSXdNak10TURZdE1ERlVNRGs2TWpjNk5EVmFJaXdpWlhod2FYSmhkR2x2YmtSaGRHVWlPaUl5TURJekxUQTNMVEF4VkRBNU9qSTNPalExV2lJc0ltTnlaV1JsYm5ScFlXeFRZMmhsYldFaU9uc2lhV1FpT2lKb2RIUndjem92TDNKaGR5NW5hWFJvZFdKMWMyVnlZMjl1ZEdWdWRDNWpiMjB2ZDJGc2RDMXBaQzkzWVd4MGFXUXRjM05wYTJsMExYWmpiR2xpTDIxaGMzUmxjaTl6Y21NdmRHVnpkQzl5WlhOdmRYSmpaWE12YzJOb1pXMWhjeTlXWlhKcFptbGhZbXhsU1dRdWFuTnZiaUlzSW5SNWNHVWlPaUpHZFd4c1NuTnZibE5qYUdWdFlWWmhiR2xrWVhSdmNqSXdNakVpZlN3aVkzSmxaR1Z1ZEdsaGJGTjFZbXBsWTNRaU9uc2lhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9WMko0YUZocmNWSkdTRk5EVkRSMVEybHlSVTUxTm5oTWRraGpaV1pJTVRSb1dYcFNRbmxCZDBOR05HWWlMQ0pqZFhKeVpXNTBRV1JrY21WemN5STZXeUl4SUVKdmRXeGxkbUZ5WkNCa1pTQnNZU0JNYVdKbGNuVERxU3dnTlRrNE1EQWdUR2xzYkdVaVhTd2laR0YwWlU5bVFtbHlkR2dpT2lJeE9Ua3pMVEEwTFRBNElpd2labUZ0YVd4NVRtRnRaU0k2SWtSUFJTSXNJbVpwY25OMFRtRnRaU0k2SWtwaGJtVWlMQ0puWlc1a1pYSWlPaUpHUlUxQlRFVWlMQ0p1WVcxbFFXNWtSbUZ0YVd4NVRtRnRaVUYwUW1seWRHZ2lPaUpLWVc1bElFUlBSU0lzSW5CbGNuTnZibUZzU1dSbGJuUnBabWxsY2lJNklqQTVNRFF3TURnd09EUklJaXdpY0d4aFkyVlBaa0pwY25Sb0lqb2lURWxNVEVVc0lFWlNRVTVEUlNJc0luQnliM0JsY25ScFpYTWlPbnNpWTNWeWNtVnVkRUZrWkhKbGMzTWlPbHNpTVNCQ2IzVnNaWFpoY21RZ1pHVWdiR0VnVEdsaVpYSjB3NmtzSURVNU9EQXdJRXhwYkd4bElsMHNJbVJoZEdWUFprSnBjblJvSWpvaU1UazVNeTB3TkMwd09DSXNJbVpoYldsc2VVNWhiV1VpT2lKRVQwVWlMQ0ptYVhKemRFNWhiV1VpT2lKS1lXNWxJaXdpWjJWdVpHVnlJam9pUmtWTlFVeEZJaXdpYm1GdFpVRnVaRVpoYldsc2VVNWhiV1ZCZEVKcGNuUm9Jam9pU21GdVpTQkVUMFVpTENKd1pYSnpiMjVoYkVsa1pXNTBhV1pwWlhJaU9pSXdPVEEwTURBNE1EZzBTQ0lzSW5Cc1lXTmxUMlpDYVhKMGFDSTZJa3hKVEV4RkxDQkdVa0ZPUTBVaWZYMHNJbVYyYVdSbGJtTmxJanBiZXlKa2IyTjFiV1Z1ZEZCeVpYTmxibU5sSWpwYklsQm9lWE5wWTJGc0lsMHNJbVYyYVdSbGJtTmxSRzlqZFcxbGJuUWlPbHNpVUdGemMzQnZjblFpWFN3aWMzVmlhbVZqZEZCeVpYTmxibU5sSWpvaVVHaDVjMmxqWVd3aUxDSjBlWEJsSWpwYklrUnZZM1Z0Wlc1MFZtVnlhV1pwWTJGMGFXOXVJbDBzSW5abGNtbG1hV1Z5SWpvaVpHbGtPbVZpYzJrNk1rRTVRbG81VTFWbE5rSmhkR0ZqVTNCMmN6RldOVU5rYWtoMlRIQlJOMkpGYzJreVNtSTJUR1JJUzI1UmVHRk9JbjFkZlN3aWFuUnBJam9pZFhKdU9uVjFhV1E2TlRsa1lXSmxZekF0WVRabU1TMDBORFUxTFRobU0yWXRZekV6T1RVMVpqSTNZbUpoSW4wLnFTcnRPNTdiRE90OHdDVjl1VTNYbjBZNmcyb3UwWTduNXRaUVhwSGpkeEc0QlNOX2FucXlvTl9qZUt4LWN5MWx2T3N1TC04VVVhSmhiVGliSVREdC1RIl19LCJleHAiOjE2ODcyNDU5MjUsImlhdCI6MTY4NzI0NTMyNSwianRpIjoidXJuOnV1aWQ6OTRhNzJmZDUtZjZkYi00NzRjLTg5ZDctODczZDljOTEzN2FhIn0.SGvGSRw1pwVuaGEt2jtPURYvTkpZeNiNGbW4rBLD40GGs1PhAS9segI87pwg5TCUClDes_6Je_wiLCIYHpkRJA"
        val siopService: SiopService = Mockito.mock(SiopServiceImpl::class.java)
        // Mock the behavior of generateDescriptorMap
        val method = SiopServiceImpl::class.java.getDeclaredMethod("generateDescriptorMap", String::class.java)
        method.isAccessible = true
        val result = method.invoke(siopService, vpToken)
        // Verify the result
        val expectedDescriptorMap = DescriptorMapping(
            id = "urn:uuid:94a72fd5-f6db-474c-89d7-873d9c9137aa",
            format = JWT_VP,
            path = "$",
            path_nested = DescriptorMapping(
                id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                format = JWT_VC,
                path = "$.verifiableCredential[0]",
            )
        )
        Assertions.assertEquals(expectedDescriptorMap, result)
    }
}
