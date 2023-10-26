package es.in2.wallet.services

import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.TokenVerificationService
import es.in2.wallet.wca.service.impl.SiopServiceImpl
import es.in2.wallet.api.util.JWT_VC
import es.in2.wallet.api.util.JWT_VP
import id.walt.model.dif.DescriptorMapping
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class SiopServiceImplTest {

    private var tokenVerificationService: TokenVerificationService = Mockito.mock(TokenVerificationService::class.java)
    private var orionService: OrionService = Mockito.mock(OrionService::class.java)
    private var siopService: SiopService = Mockito.mock(SiopServiceImpl::class.java)
    private val userUUID = UUID.randomUUID()
    private val siopAuthRequestUri = "https://portal.packetdelivery.com/authentication-requests?state=af0ifjsldkj"
    private val siopAuthRequest = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

    private val vpToken = "eyJraWQiOiJkaWQ6a2V5OnpEbmFlVjFyUjdHSzN3Z2Q5c2lRYVFtWWtQa3gzM0VxUHFqTFU2QlpWdnZYOENnaHAjekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2In0.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlVjFyUjdHSzN3Z2Q5c2lRYVFtWWtQa3gzM0VxUHFqTFU2QlpWdnZYOENnaHAiLCJuYmYiOjE2ODcyNDUzMjUsImlzcyI6ImRpZDprZXk6ekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInZwIjp7InR5cGUiOlsiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiJdLCJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJpZCI6InVybjp1dWlkOjk0YTcyZmQ1LWY2ZGItNDc0Yy04OWQ3LTg3M2Q5YzkxMzdhYSIsImhvbGRlciI6ImRpZDprZXk6ekRuYWVWMXJSN0dLM3dnZDlzaVFhUW1Za1BreDMzRXFQcWpMVTZCWlZ2dlg4Q2docCIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbImV5SnJhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9jalUyV2xkTFFtZzJNWFY0V2pSVVMwZDJPRE5TVUhsTU9IVjJObmhqUjNSVFVrcFJWbU4zUVhKaGFUUWplbEV6YzJoeU5UWmFWMHRDYURZeGRYaGFORlJMUjNZNE0xSlFlVXc0ZFhZMmVHTkhkRk5TU2xGV1kzZEJjbUZwTkNJc0luUjVjQ0k2SWtwWFZDSXNJbUZzWnlJNklrVlRNalUyU3lKOS5leUp6ZFdJaU9pSmthV1E2YTJWNU9ucFJNM05vVjJKNGFGaHJjVkpHU0ZORFZEUjFRMmx5UlU1MU5uaE1ka2hqWldaSU1UUm9XWHBTUW5sQmQwTkdOR1lpTENKdVltWWlPakUyT0RVMk1URTJOalVzSW1semN5STZJbVJwWkRwclpYazZlbEV6YzJoeU5UWmFWMHRDYURZeGRYaGFORlJMUjNZNE0xSlFlVXc0ZFhZMmVHTkhkRk5TU2xGV1kzZEJjbUZwTkNJc0ltVjRjQ0k2TVRZNE9ESXdNelkyTlN3aWFXRjBJam94TmpnMU5qRXhOalkxTENKMll5STZleUowZVhCbElqcGJJbFpsY21sbWFXRmliR1ZEY21Wa1pXNTBhV0ZzSWl3aVZtVnlhV1pwWVdKc1pVRjBkR1Z6ZEdGMGFXOXVJaXdpVm1WeWFXWnBZV0pzWlVsa0lsMHNJa0JqYjI1MFpYaDBJanBiSW1oMGRIQnpPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1ERTRMMk55WldSbGJuUnBZV3h6TDNZeElsMHNJbWxrSWpvaWRYSnVPblYxYVdRNk5UbGtZV0psWXpBdFlUWm1NUzAwTkRVMUxUaG1NMll0WXpFek9UVTFaakkzWW1KaElpd2lhWE56ZFdWeUlqb2laR2xrT210bGVUcDZVVE56YUhJMU5scFhTMEpvTmpGMWVGbzBWRXRIZGpnelVsQjVURGgxZGpaNFkwZDBVMUpLVVZaamQwRnlZV2swSWl3aWFYTnpkV0Z1WTJWRVlYUmxJam9pTWpBeU15MHdOaTB3TVZRd09Ub3lOem8wTlZvaUxDSnBjM04xWldRaU9pSXlNREl6TFRBMkxUQXhWREE1T2pJM09qUTFXaUlzSW5aaGJHbGtSbkp2YlNJNklqSXdNak10TURZdE1ERlVNRGs2TWpjNk5EVmFJaXdpWlhod2FYSmhkR2x2YmtSaGRHVWlPaUl5TURJekxUQTNMVEF4VkRBNU9qSTNPalExV2lJc0ltTnlaV1JsYm5ScFlXeFRZMmhsYldFaU9uc2lhV1FpT2lKb2RIUndjem92TDNKaGR5NW5hWFJvZFdKMWMyVnlZMjl1ZEdWdWRDNWpiMjB2ZDJGc2RDMXBaQzkzWVd4MGFXUXRjM05wYTJsMExYWmpiR2xpTDIxaGMzUmxjaTl6Y21NdmRHVnpkQzl5WlhOdmRYSmpaWE12YzJOb1pXMWhjeTlXWlhKcFptbGhZbXhsU1dRdWFuTnZiaUlzSW5SNWNHVWlPaUpHZFd4c1NuTnZibE5qYUdWdFlWWmhiR2xrWVhSdmNqSXdNakVpZlN3aVkzSmxaR1Z1ZEdsaGJGTjFZbXBsWTNRaU9uc2lhV1FpT2lKa2FXUTZhMlY1T25wUk0zTm9WMko0YUZocmNWSkdTRk5EVkRSMVEybHlSVTUxTm5oTWRraGpaV1pJTVRSb1dYcFNRbmxCZDBOR05HWWlMQ0pqZFhKeVpXNTBRV1JrY21WemN5STZXeUl4SUVKdmRXeGxkbUZ5WkNCa1pTQnNZU0JNYVdKbGNuVERxU3dnTlRrNE1EQWdUR2xzYkdVaVhTd2laR0YwWlU5bVFtbHlkR2dpT2lJeE9Ua3pMVEEwTFRBNElpd2labUZ0YVd4NVRtRnRaU0k2SWtSUFJTSXNJbVpwY25OMFRtRnRaU0k2SWtwaGJtVWlMQ0puWlc1a1pYSWlPaUpHUlUxQlRFVWlMQ0p1WVcxbFFXNWtSbUZ0YVd4NVRtRnRaVUYwUW1seWRHZ2lPaUpLWVc1bElFUlBSU0lzSW5CbGNuTnZibUZzU1dSbGJuUnBabWxsY2lJNklqQTVNRFF3TURnd09EUklJaXdpY0d4aFkyVlBaa0pwY25Sb0lqb2lURWxNVEVVc0lFWlNRVTVEUlNJc0luQnliM0JsY25ScFpYTWlPbnNpWTNWeWNtVnVkRUZrWkhKbGMzTWlPbHNpTVNCQ2IzVnNaWFpoY21RZ1pHVWdiR0VnVEdsaVpYSjB3NmtzSURVNU9EQXdJRXhwYkd4bElsMHNJbVJoZEdWUFprSnBjblJvSWpvaU1UazVNeTB3TkMwd09DSXNJbVpoYldsc2VVNWhiV1VpT2lKRVQwVWlMQ0ptYVhKemRFNWhiV1VpT2lKS1lXNWxJaXdpWjJWdVpHVnlJam9pUmtWTlFVeEZJaXdpYm1GdFpVRnVaRVpoYldsc2VVNWhiV1ZCZEVKcGNuUm9Jam9pU21GdVpTQkVUMFVpTENKd1pYSnpiMjVoYkVsa1pXNTBhV1pwWlhJaU9pSXdPVEEwTURBNE1EZzBTQ0lzSW5Cc1lXTmxUMlpDYVhKMGFDSTZJa3hKVEV4RkxDQkdVa0ZPUTBVaWZYMHNJbVYyYVdSbGJtTmxJanBiZXlKa2IyTjFiV1Z1ZEZCeVpYTmxibU5sSWpwYklsQm9lWE5wWTJGc0lsMHNJbVYyYVdSbGJtTmxSRzlqZFcxbGJuUWlPbHNpVUdGemMzQnZjblFpWFN3aWMzVmlhbVZqZEZCeVpYTmxibU5sSWpvaVVHaDVjMmxqWVd3aUxDSjBlWEJsSWpwYklrUnZZM1Z0Wlc1MFZtVnlhV1pwWTJGMGFXOXVJbDBzSW5abGNtbG1hV1Z5SWpvaVpHbGtPbVZpYzJrNk1rRTVRbG81VTFWbE5rSmhkR0ZqVTNCMmN6RldOVU5rYWtoMlRIQlJOMkpGYzJreVNtSTJUR1JJUzI1UmVHRk9JbjFkZlN3aWFuUnBJam9pZFhKdU9uVjFhV1E2TlRsa1lXSmxZekF0WVRabU1TMDBORFUxTFRobU0yWXRZekV6T1RVMVpqSTNZbUpoSW4wLnFTcnRPNTdiRE90OHdDVjl1VTNYbjBZNmcyb3UwWTduNXRaUVhwSGpkeEc0QlNOX2FucXlvTl9qZUt4LWN5MWx2T3N1TC04VVVhSmhiVGliSVREdC1RIl19LCJleHAiOjE2ODcyNDU5MjUsImlhdCI6MTY4NzI0NTMyNSwianRpIjoidXJuOnV1aWQ6OTRhNzJmZDUtZjZkYi00NzRjLTg5ZDctODczZDljOTEzN2FhIn0.SGvGSRw1pwVuaGEt2jtPURYvTkpZeNiNGbW4rBLD40GGs1PhAS9segI87pwg5TCUClDes_6Je_wiLCIYHpkRJA"

//    @Test
//    fun testGetSiopAuthenticationRequest() {
//        // Mock the behavior of getSiopAuthenticationRequestInJwsFormat
//        `when`(siopService.getSiopAuthenticationRequest(siopAuthRequestUri))
//            .thenReturn(mutableListOf("VerifiableId"))
//        // Mock the behavior of getAuthRequestClaim
//        val siopAuthenticationRequest = "siopAuthenticationRequest"
//        `when`(siopService.processSiopAuthenticationRequest(siopAuthRequestUri))
//            .thenReturn(mutableListOf("VerifiableId"))
//        // Mock the behavior of processSiopAuthenticationRequest
//        val result = mutableListOf("VerifiableId")
//        `when`(siopService.processSiopAuthenticationRequest(siopAuthenticationRequest))
//            .thenReturn(result)
//        // Call the getSiopAuthenticationRequest method
//        val response = siopService.getSiopAuthenticationRequest(siopAuthRequestUri)
//        // Verify the result
//        assertEquals(result, response)
//    }

    @Test
    fun testGenerateDescriptorMap(){
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
        assertEquals(expectedDescriptorMap, result)
    }

}
