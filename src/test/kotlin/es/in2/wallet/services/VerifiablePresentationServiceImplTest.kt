package es.in2.wallet.services

import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.service.impl.VerifiablePresentationServiceImpl
import es.in2.wallet.wca.util.ApplicationUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class VerifiablePresentationServiceImplTest {
    private val applicationUtils: ApplicationUtils = mockk(relaxed = true)

    private lateinit var verifiablePresentationServiceImplTest :VerifiablePresentationServiceImpl
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        verifiablePresentationServiceImplTest = VerifiablePresentationServiceImpl("walletDataBaseUrl", "walletCryotoBaseUrl", applicationUtils)
    }

    @Test
    fun testCreateVerifiablePresentation() {
        val vc = "eyJraWQiOiJkaWQ6a2V5OnpRM3NoUGo2THlOZU1iemt3cTJFc3BHcGJONlN3Q2dvYTFmeFBWNkVHeHdpdDJtb1ojelEzc2hQajZMeU5lTWJ6a3dxMkVzcEdwYk42U3dDZ29hMWZ4UFY2RUd4d2l0Mm1vWiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlVmViWDh0YmR1WHI2S2VYeEx1bkNvazF1cG9ZM25lUjNuZWhwZVIxYXVqbjEiLCJuYmYiOjE2OTk0NDM0OTMsImlzcyI6ImRpZDprZXk6elEzc2hQajZMeU5lTWJ6a3dxMkVzcEdwYk42U3dDZ29hMWZ4UFY2RUd4d2l0Mm1vWiIsImV4cCI6MTcwMjAzNTQ5MywiaWF0IjoxNjk5NDQzNDkzLCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiTEVBUkNyZWRlbnRpYWwiXSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL2RvbWUtbWFya2V0cGxhY2UuZXUvLzIwMjIvY3JlZGVudGlhbHMvbGVhcmNyZWRlbnRpYWwvdjEiXSwiaWQiOiJ1cm46dXVpZDpiMmI4MmIwYi05NWYzLTQ1OWEtYWZkOC1kZTI1ZWJhYzY3MjYiLCJpc3N1ZXIiOnsiaWQiOiJkaWQ6a2V5OnpRM3NoUGo2THlOZU1iemt3cTJFc3BHcGJONlN3Q2dvYTFmeFBWNkVHeHdpdDJtb1oifSwiaXNzdWFuY2VEYXRlIjoiMjAyMy0xMS0wOFQxMTozODoxM1oiLCJpc3N1ZWQiOiIyMDIzLTExLTA4VDExOjM4OjEzWiIsInZhbGlkRnJvbSI6IjIwMjMtMTEtMDhUMTE6Mzg6MTNaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTEyLTA4VDExOjM4OjEzWiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6RG5hZVZlYlg4dGJkdVhyNktlWHhMdW5Db2sxdXBvWTNuZVIzbmVocGVSMWF1am4xIiwidGl0bGUiOiJNci4iLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImdlbmRlciI6Ik0iLCJwb3N0YWxfYWRkcmVzcyI6IiIsImVtYWlsIjoiam9obmRvZUBnb29kYWlyLmNvbSIsInRlbGVwaG9uZSI6IiIsImZheCI6IiIsIm1vYmlsZV9waG9uZSI6IiszNDc4NzQyNjYyMyIsImxlZ2FsUmVwcmVzZW50YXRpdmUiOnsiY24iOiI1NjU2NTY1NlYgSmVzdXMgUnVpeiIsInNlcmlhbE51bWJlciI6IjU2NTY1NjU2ViIsIm9yZ2FuaXphdGlvbklkZW50aWZpZXIiOiJWQVRFUy0xMjM0NTY3OCIsIm8iOiJHb29kQWlyIiwiYyI6IkVTIn0sInJvbGVzQW5kRHV0aWVzIjpbeyJ0eXBlIjoiTEVBUkNyZWRlbnRpYWwiLCJpZCI6Imh0dHBzOi8vZG9tZS1tYXJrZXRwbGFjZS5ldS8vbGVhci92MS82NDg0OTk0bjRyOWU5OTA0OTQifV0sImtleSI6InZhbHVlIn19LCJqdGkiOiJ1cm46dXVpZDpiMmI4MmIwYi05NWYzLTQ1OWEtYWZkOC1kZTI1ZWJhYzY3MjYifQ.zyYxH5lfce2OHipl0V1B7BBZhdh2GbA7spgwVQoRo1i8pFuUP61-iG_VbyptAd63qkdqOHHbOih6ljveQ7lD9g"

        every {
            applicationUtils.getRequest(any(), any())
        } returns vc

/*
        ServiceMatrix(SERVICE_MATRIX)
        val keyId = KeyService.getService().generate(KeyAlgorithm.ECDSA_Secp256r1)
        val holderDid = DidService.create(DidMethod.key, keyId.id)
        every {
            applicationUtils.postRequest(any(), any(), any())
        } returns holderDid
*/
        val vcSelectorResponseDTO = VcSelectorResponseDTO(
            redirectUri = "http://localhost:8087/api/verifier/siop-sessions",
            state = "1QvFqRKZTI2JJTnXb74cOQ",
            selectedVcList = listOf(
                VcBasicDataDTO(
                    id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                    vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
                    credentialSubject = "default data"
                )
            )
        )

        val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTk0NDI5MzMsImlhdCI6MTY5OTQ0MjYzMywianRpIjoiZTg1ODRiZGMtNGZjMS00OWU0LWIyNmItZmEzNTBmNGM4NmNmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOWY5Y2MyZGEtOWM3My00YTkwLWJmYzMtOWM3Y2VmM2UwZjA1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJkN2MyMjNhNi1lMTQ4LTRjNzUtODlhZi0zMzI3MjEzNjcyMGQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZDdjMjIzYTYtZTE0OC00Yzc1LTg5YWYtMzMyNzIxMzY3MjBkIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MDQiLCJlbWFpbCI6InRlc3QwNEBnbWFpbC5jb20ifQ.t7EYK93ENT-5v2i5KUaSGAzOLaa1OxV6cCLSJ6aQKB20WAfAxmYLJewWs9mmdAEwqWe4bwkbdpFAPyalIZVySc6PuVV4DWy0ge1C_PzRCeS2MQ5jU_KYBfTIaZHE5PUrUs75-MChHP2IcTGc3Tl2cJ3WaojWkWg--sIobDokhq9_okfejIE7M-QDDWHVSlljZnq0nOsXuCNIdcQ6QXHikoR_DpS9AmW-orWFMeu1ENjk7Dc1c3UfVTAIs1dB84QldYSvWm1ezPnFInd_fLaw6hvfqXCvabbbHZu6JFijf_hf5JqhKoLXpD3t4gB2EP9U7ZpJUfoG_2JqTLLwNgbMyg"

        verifiablePresentationServiceImplTest.createVerifiablePresentation(vcSelectorResponseDTO, token)

    }

}