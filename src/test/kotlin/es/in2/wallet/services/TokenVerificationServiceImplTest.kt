package es.in2.wallet.services

import es.in2.wallet.wca.exception.JwtInvalidFormatException
import es.in2.wallet.wca.service.impl.TokenVerificationServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TokenVerificationServiceImplTest {

    @InjectMocks
    private lateinit var tokenVerificationServiceImpl: TokenVerificationServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testVerifySiopAuthRequestAsJwsFormat_invalidTokenFormat() {

        // Prepare test data
        val requestToken = "testRequestToken"

        // Call the method to be tested
        try {
            tokenVerificationServiceImpl.verifySiopAuthRequestAsJwsFormat(requestToken) // This should throw an exception
            // Fail the test if no exception is thrown
            throw AssertionError("Expected JwtInvalidFormatException, but no exception was thrown")
        } catch (e: JwtInvalidFormatException) {
            // Check the exception message if needed
            Assertions.assertEquals("The 'request_token' has an invalid format", e.message)
        }
    }

    @Test
    fun testVerifySiopAuthRequestAsJwsFormat_validTokenFormat() {
        val requestToken = "eyJraWQiOiJkaWQ6a2V5OnpEbmFlWTdycmNTd3lhdnluY0pWMTF2QUdSMmo0d3RocTZ1WHpZVm9jZTUxZllwVEUiLCJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJhdXRoX3JlcXVlc3QiOiJvcGVuaWQ6Ly8_c2NvcGU9W0xFQVJDcmVkZW50aWFsXSZyZXNwb25zZV90eXBlPXZwX3Rva2VuJnJlc3BvbnNlX21vZGU9ZGlyZWN0X3Bvc3QmY2xpZW50X2lkPWRpZDprZXk6ekRuYWVZN3JyY1N3eWF2eW5jSlYxMXZBR1IyajR3dGhxNnVYellWb2NlNTFmWXBURSZzdGF0ZT13cGcySzhTeFQ2U2J4SDVjNjlLTnpnJm5vbmNlPTc3ZTBjOGY5LTdiOWMtNDc5MC1hZGY0LWMwYTI2NDNiODI2MSZyZWRpcmVjdF91cmk9aHR0cDovL3BvcnRhbC1hcGk6ODA4Ny9hcGkvdmVyaWZpZXIvc2lvcC1zZXNzaW9ucyIsInN1YiI6ImRpZDprZXk6ekRuYWVZN3JyY1N3eWF2eW5jSlYxMXZBR1IyajR3dGhxNnVYellWb2NlNTFmWXBURSIsImF1ZCI6Imh0dHBzOi8vc2VsZi1pc3N1ZWQubWUvdjIiLCJpc3MiOiJkaWQ6a2V5OnpEbmFlWTdycmNTd3lhdnluY0pWMTF2QUdSMmo0d3RocTZ1WHpZVm9jZTUxZllwVEUiLCJleHAiOjE2OTk0Nzc3MjAsImlhdCI6MTY5OTQ3NzEyMH0.iEUeZp5CIfmw5w1lIPm08Z22MoWQgAy8Utp5T2aDB2LNb9wc_ptqLmCfqj0_oAtoH9ws_7-zrTpu0Osl2dE6aA"

        tokenVerificationServiceImpl.verifySiopAuthRequestAsJwsFormat(requestToken)
    }
}