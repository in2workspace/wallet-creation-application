package es.in2.wallet.services

import com.nimbusds.jose.Payload
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.wca.service.TokenVerificationService
import id.walt.services.did.DidService
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TokenVerificationServiceImplTest {

    @Mock
    private lateinit var payload: Payload
    @Mock
    private lateinit var signedJWTResponse: SignedJWT
    private var didService: DidService = Mockito.mock(DidService::class.java)
    private var tokenVerificationService: TokenVerificationService = Mockito.mock(TokenVerificationService::class.java)


}